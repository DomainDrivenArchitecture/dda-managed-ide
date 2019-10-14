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

(ns dda.pallet.dda-managed-ide.infra.python
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [selmer.parser :as selmer]
    [pallet.action :as action]
    [pallet.actions :as actions]
    [dda.config.commons.user-home :as user-env]))

(def Settings
   #{:install-pip3
     :install-pybuilder
     :install-jupyterlab})

(defn install-pip3
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-pip3")))
  (actions/packages
    :aptitude ["python3-pip"])
  (actions/exec-checked-script
    "install pip3"
    ("pip3" "install" "pip" "--upgrade")))

(defn install-pybuilder
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-pybuilder")))
  (actions/exec-checked-script
   "install pybuilder"
   ("pip3" "install" "pybuilder")
   ("pip3" "install" "ddadevops")
   ("pip3" "install" "pypandoc")
   ("pip3" "install" "mockito")
   ("pip3" "install" "coverage")
   ("pip3" "install" "unittest-xml-reporting")
   ("pip3" "install" "unittest-xml-reporting")
   ("pip3" "install" "pip" "--upgrade")))

(defn install-rest-client
  [facility]
  (actions/as-action
   (logging/info (str facility "-install system: install-rest-client")))
  (actions/exec-checked-script
   "install pybuilder"
   ("pip3" "install" "requests")
   ("pip3" "install" "pip" "--upgrade")))

(defn install-jupyterlab
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-pybuilder")))
  (actions/exec-checked-script
    "install jupyterlab"
    ("pip3" "install" "jupyterlab")
    ("pip3" "install" "pandas")
    ("pip3" "install" "matplotlib")))

(s/defn install-system
  [facility :- s/Keyword
   settings]
  (let []
    (when (contains? settings :install-pip3)
       (install-pip3 facility))
    (when (contains? settings :install-pybuilder)
       (install-pybuilder facility))
    (when (contains? settings :install-jupyterlab)
       (install-jupyterlab facility))))
