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
    [pallet.action :as action]
    [pallet.actions :as actions]
    [dda.config.commons.user-home :as user-env]))

(def Aws
   {(s/optional-key :simple) {:id s/Str
                              :secret s/Str}})

(def Terraform
   {:version s/Str
    (s/optional-key :sha256-hash) s/Str})

(def Packer
   {:version s/Str
    (s/optional-key :sha256-hash) s/Str})

(def Docker
   {:bip s/Str})

(def Devops {(s/optional-key :terraform) Terraform
             (s/optional-key :aws) Aws
             (s/optional-key :docker) Docker
             (s/optional-key :packer) Packer})

(def Settings
   #{:install-mfa
     :install-ami-cleaner})

;TODO: Add pybuilder installation and configuration:
;sudo apt install python3-pip
;sudo pip3 install pip --upgrade
;pip install pybuilder --user
;export PATH=$PATH:~/.local/bin

(defn install-mfa
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-mfa")))
  (actions/packages
    :aptitude ["clipit" "python-pip"])
  (actions/exec-checked-script
    "install mfa"
    ("pip" "install" "mfa")))

(defn install-ami-cleaner
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-ami-cleaner")))
  (actions/packages
    :aptitude ["python-pip"])
  (actions/exec-checked-script
    "install ami-cleaner"
    ("pip" "install" "future")
    ("pip" "install" "aws-amicleaner")))

(s/defn install-packer
  [facility :- s/Keyword
   config :- Packer]
  (let [{:keys [version sha256-hash]} config
        file-name (str "packer_" version "_linux_amd64.zip")
        sum-name (str file-name "SHA256SUM")]
    (actions/as-action
      (logging/info (str facility "-install system: install-packer")))
    (when (contains? config :sha256-hash)
      (actions/remote-file
        (str "/tmp/" sum-name)
        :owner "root"
        :group "root"
        :mode "600"
        :literal true
        :content (str sha256-hash " " file-name)))
    (actions/exec-checked-script
      "install packer"
      ("curl" "-L" "-o" ~(str "/tmp/" file-name)
        ~(str "https://releases.hashicorp.com/packer/" version "/" file-name))
      ("cd" "/tmp")
      (if (file-exists? ~sum-name)
        ("sha256sum" "-c" ~sum-name))
      ("unzip" ~file-name)
      ("mv" "packer" "/usr/local/bin/"))))

(defn install-docker
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-docker")))
  (actions/packages
    :aptitude ["docker.io"]))

(s/defn configure-system-docker
  [facility :- s/Keyword
   docker :- Docker]
  (actions/as-action
    (logging/info (str facility "-configure system: configure-system-docker")))
  (actions/directory
    "/etc/docker"
    :owner "root"
    :group "root"
    :mode "755")
  (actions/remote-file
    "/etc/docker/daemon.json"
    :owner "root"
    :group "root"
    :mode "644"
    :literal true
    :content
    (selmer/render-file "docker_deamon.json.template" docker)))

(s/defn configure-user-docker
  [facility :- s/Keyword
   user-name :- s/Str]
  (actions/as-action
    (logging/info (str facility "-configure user: configure-user-docker")))
  (actions/exec-checked-script
    "add user to docker group"
    ("usermod" "-a" "-G" "docker" ~user-name)))

(defn install-awscli
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-awscli")))
  (actions/packages
    :aptitude ["awscli"]))

(s/defn aws-credentials-configuration
  [aws :- Aws]
  (when (contains? aws :simple)
    (selmer/render-file "aws_simple_credentials.template" aws)))

(s/defn configure-user-aws
  [facility :- s/Keyword
   os-user-name :- s/Str
   aws :- Aws]
  (let [path (str (user-env/user-home-dir os-user-name) "/.aws/")]
    (actions/as-action
      (logging/info (str facility "-configure user: configure-aws")))
    (actions/directory
      path
      :owner os-user-name
      :group os-user-name
      :mode "755")
    (when (contains? aws :simple)
      (actions/remote-file
        (str path "credentials")
        :owner os-user-name
        :group os-user-name
        :mode "600"
        :literal true
        :content
        (aws-credentials-configuration aws)))))

(s/defn install-terraform
  [facility
   terraform-config :- Terraform]
  (let [{:keys [version sha256-hash]} terraform-config
        terraform-file-name (str "terraform_" version "_linux_amd64.zip")
        terraform-sum-name (str terraform-file-name "SHA256SUM")]
    (actions/as-action
      (logging/info (str facility "-install system: install-terraform")))
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
   contains-devops? :- s/Bool
   devops :- Devops]
  (let [{:keys [terraform packer]} devops]
    (when (contains? settings :install-mfa)
       (install-mfa facility))
    (when (contains? settings :install-ami-cleaner)
       (install-ami-cleaner facility))
    (when contains-devops?
      (when (contains? devops :aws)
         (install-awscli facility))
      (when (contains? devops :docker)
         (install-docker facility))
      (when (contains? devops :terraform)
         (install-terraform facility terraform))
      (when (contains? devops :packer)
         (install-packer facility packer)))))

(s/defn configure-system
  [facility :- s/Keyword
   contains-devops? :- s/Bool
   devops :- Devops
   os-user-name :- s/Str]
  (let [{:keys [docker]} devops]
    (when contains-devops?
      (when (contains? devops :docker)
        (do
          (configure-system-docker facility docker)
          (configure-user-docker facility os-user-name))))))

(s/defn configure-user
  [facility :- s/Keyword
   os-user-name :- s/Str
   contains-devops? :- s/Bool
   devops :- Devops]
  (let [{:keys [aws docker]} devops]
    (when contains-devops?
      (when (contains? devops :aws)
        (configure-user-aws facility os-user-name aws)))))
