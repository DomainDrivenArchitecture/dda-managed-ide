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

;TODO: Add pybuilder installation and configuration:
;sudo apt install python3-pip
;sudo pip3 install pip --upgrade
;export PATH=$PATH:~/.local/bin
;pip install pybuilder --user
;pip install jupyterlab --user
;pip install pandas --user
;pip install matplottlib -user

;TODO: remove python3-pip from devops ...
