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

(ns dda.pallet.dda-managed-ide.infra.basics
  (:require
    [clojure.string :as string]
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.actions :as actions]
    [dda.pallet.crate.util :as util]
    [dda.config.commons.user-home :as user-env]))

(def ArgoUml
   {:version s/Str})

(def Yed
   {:download-url s/Str})

(def Basics
  {(s/optional-key :argo-uml) ArgoUml
   (s/optional-key :yed) Yed})

(def Settings
   #{:install-basics :install-tmate})

(s/defn install-basics
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "-install system: install-basics")))
  (actions/packages
    :aptitude ["curl" "gnutls-bin" "apache2-utils" "meld" "whois" "make"]))

(s/defn install-tmate
  [facility :- s/Keyword]
  (actions/as-action
   (logging/info (str facility "-install system: install-tmate")))
  (actions/packages
   :aptitude ["tmate"]))

(s/defn
  install-argouml
  "get and install argouml at /opt/argouml"
  [facility :- s/Keyword
   config :- ArgoUml]
  (let [{:keys [version]} config]
    (actions/as-action
      (logging/info (str facility "-configure system: install-argouml")))
    (actions/remote-directory
      "/opt/argouml"
      :owner "root"
      :group "users"
      :recursive true
      :unpack :tar
      :url (str "http://argouml-downloads.tigris.org/nonav/argouml-" version
                "/ArgoUML-" version ".tar.gz"))
    (actions/remote-file
      "/etc/profile.d/argouml.sh"
      :literal true
      :content
      (util/create-file-content
        ["PATH=$PATH:/opt/argouml"
         "export PATH"]))))

(s/defn
  install-yed
  "get and install yed at /opt/yed"
  [facility :- s/Keyword
   config :- Yed]
  (let [{:keys [download-url]} config]
    (actions/as-action
      (logging/info (str facility "-configure system: install-yed")))
    (actions/remote-directory
      "/opt/yed"
      :owner "root"
      :group "users"
      :recursive true
      :unpack :unzip
      :url download-url)
    (actions/remote-file
      "/opt/yed/yed.sh"
      :literal true
      :content
      (util/create-file-content
        ["#!/bin/bash"
         "java -jar yed.jar"]))))

(s/defn install-system
  [facility :- s/Keyword
   ide-settings
   contains-basics? :- s/Bool
   basics :- Basics]
  (let [{:keys [argo-uml yed]} basics]
    (when (contains? ide-settings :install-basics)
      (install-basics facility))
    (when (contains? ide-settings :install-tmate)
      (install-tmate facility))
    (when contains-basics?
      (when (contains? basics :argo-uml)
        (install-argouml facility argo-uml))
      (when (contains? basics :yed)
        (install-yed facility yed)))))
