; Licensed to the Apache Software Foundation (ASF) under one
; or more contributor license agreements. See the NOTICE file
; distributed with this work for additional information
; regarding copyright ownership. The ASF licenses this file
; to you under the Apache License, Version 2.0 (the
; "License"); you may not use this file except in compliance
; with the License. You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.

(ns dda.pallet.dda-managed-ide.infra.devops
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [selmer.parser :as selmer]
    [pallet.actions :as actions]
    [dda.config.commons.user-home :as user-env]))

(def AwsCredentials
   {(s/optional-key :simple) {:id s/Str
                              :secret s/Str}})

(def Terraform
   {:version s/Str
    (s/optional-key :sha256-hash) s/Str})

(def Settings
   #{
     :install-mfa
     :install-mach
     :install-awscli
     :install-terraform})

(defn install-mach
  [facility]
  (actions/as-action
    (logging/info (str facility " install system: install-mach")))
  (actions/packages
    :aptitude ["npm"])
  (actions/exec-checked-script
    "install mach"
    ("npm" "install" "-g" "@juxt/mach")
    ("cd" "/usr/local/bin")
    ("curl" "-fsSLo" "boot"
            "https://github.com/boot-clj/boot-bin/releases/download/latest/boot.sh")
    ("chmod" "755" "boot")))

(defn install-mfa
  [facility]
  (actions/as-action
    (logging/info (str facility " install system: install-mfa")))
  (actions/packages
    :aptitude ["clipit" "python-pip"])
  (actions/exec-checked-script
    "install mfa"
    ("pip" "install" "mfa")))

(defn install-awscli
  [facility]
  (actions/as-action
    (logging/info (str facility " install system: install-awscli")))
  (actions/packages
    :aptitude ["awscli"]))

(s/defn aws-credentials-configuration
  [aws-credentials :- AwsCredentials]
  (when (contains? aws-credentials :simple)
    (selmer/render-file "aws_simple_credentials.template" aws-credentials)))

(s/defn configure-aws
  [facility :- s/Keyword
   os-user-name :- s/Str
   aws-credentials :- AwsCredentials]
  (let [path (str (user-env/user-home-dir os-user-name) "/.aws/")]
    (actions/as-action
      (logging/info (str facility " configure system: configure-aws")))
    (logging/info os-user-name)
    (logging/info aws-credentials)
    (logging/info (aws-credentials-configuration aws-credentials))
    (actions/directory
      path
      :owner os-user-name
      :group os-user-name
      :mode "755")
    (when (contains? aws-credentials :simple)
      (actions/remote-file
        (str path "credentials")
        :owner os-user-name
        :group os-user-name
        :mode "600"
        :literal true
        :content
        (aws-credentials-configuration aws-credentials)))))

(s/defn install-terraform
  [facility
   terraform-config :- Terraform]
  (let [{:keys [version sha256-hash]} terraform-config
        terraform-file-name (str "terraform_" version "_linux_amd64.zip")
        terraform-sum-name (str terraform-file-name "SHA256SUM")]
    (actions/as-action
      (logging/info (str facility " install system: install-terraform")))
    (when (contains? terraform-config :sha256-hash)
      (actions/remote-file
        (str "/tmp/" terraform-sum-name)
        :owner "root"
        :group "root"
        :mode "600"
        :literal true
        :content (str sha256-hash " " terraform-file-name)))
    (actions/exec-checked-script
      "install terraform"
      ("curl" "-L" "-o" ~(str "/tmp/" terraform-file-name)
        ~(str "https://releases.hashicorp.com/terraform/" version "/" terraform-file-name))
      ("cd" "/tmp")
      (if (file-exists? ~terraform-sum-name)
        ("sha256sum" "-c" ~terraform-sum-name))
      ("unzip" ~terraform-file-name)
      ("mv" "terraform" "/usr/local/bin/"))))

(s/defn install-system
  [facility :- s/Keyword
   settings
   terraform-config :- Terraform]
  (when (contains? settings :install-mach)
    (install-mach facility))
  (when (contains? settings :install-mfa)
     (install-mfa facility))
  (when (contains? settings :install-awscli)
     (install-awscli facility))
  (when (contains? settings :install-terraform)
     (install-terraform facility terraform-config)))

(s/defn configure-user
  [facility :- s/Keyword
   contains-devops? :- s/Bool
   os-user-name :- s/Str
   aws-credentials :- AwsCredentials]
  (when contains-devops?
    (configure-aws facility os-user-name aws-credentials)))
