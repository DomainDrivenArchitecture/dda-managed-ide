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
    [pallet.actions :as actions]
    [dda.config.commons.user-home :as user-env]))

(def Settings
   #{
     :install-mfa
     :install-mach
     :install-awscli})

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

(defn configure-aws
  [facility]
  (actions/as-action
    (logging/info (str facility " configure system: configure-aws")))
  (actions/packages
    :aptitude ["awscli"]))

(s/defn install-system
  [facility settings]
  (when (contains? settings :install-mach)
    (install-mach facility))
  (when (contains? ide-settings :install-mfa)
     (install-mfa facility))
  (when (contains? ide-settings :install-awscli)
     (install-awscli facility)))
