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
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.actions :as actions]
    [dda.config.commons.user-home :as user-env]))

(def Settings
   #{:install-basics
     :install-mfa
     :install-asciinema})

(defn install-basics
  [facility]
  (actions/as-action
    (logging/info (str facility "install system: install-basics")))
  (actions/packages
    :aptitude ["curl" "gnutls-bin" "apache2-utils" "meld" "whois" "make"]))

(defn install-mfa
  [facility]
  (actions/as-action
    (logging/info (str facility "configure system: install-mfa")))
  (actions/packages
    :aptitude ["clipit" "python-pip"])
  (actions/exec-checked-script
    "install mfa"
    ("pip" "install" "mfa")))

(defn install-asciinema
  [facility]
  (actions/as-action
    (logging/info (str facility "configure system: install-asciinema")))
  (actions/package-source "asciinema"
    :aptitude
    {:url "http://ppa.launchpad.net/zanchey/asciinema/ubuntu "
     :release "bionic"
     :scopes ["main"]
     :key-url "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x9D2E234C0F833EAD"})
  (actions/package-manager :update)
  (actions/packages :aptitude ["asciinema" "nodejs" "phantomjs" "imagemagick" "gifsicle"])
  (actions/exec-checked-script
    "install asciicast2gif"
    ("npm" "install" "--global" "asciicast2gif")))

(s/defn install-system
  [facility ide-settings]
  (when (contains? ide-settings :install-basics)
     (install-basics facility))
  (when (contains? ide-settings :install-mfa)
     (install-mfa facility))
  (when (contains? ide-settings :install-asciinema)
     (install-asciinema facility)))
