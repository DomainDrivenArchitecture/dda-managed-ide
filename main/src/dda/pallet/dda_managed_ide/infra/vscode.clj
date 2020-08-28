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

(ns dda.pallet.dda-managed-ide.infra.vscode
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [dda.provision :as p]))

(def Vscode {(s/optional-key :plugins) [{:plugin-name s/Str :plugin-config s/Any}]})

(def provisioner :dda.provision.pallet/pallet)

(defn copy-file-test []
  (p/copy-resources-to-tmp provisioner "" "vsc" [{:filename "install-vsc.sh"}
                                                 {:filename "install-vsc-extensions.sh"}]))

(defn install-vsc []
  (p/exec-file-on-target-as-root provisioner "" "vsc" "install-vsc.sh")
  (p/exec-file-on-target-as-user provisioner "initial" "" "vsc" "install-vsc-extensions.sh"))

(defn install-system []
  (copy-file-test)
  (install-vsc))

