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
(ns dda.pallet.dda-managed-ide.app.instantiate-existing
  (:require
    [clojure.inspector :as inspector]
    [pallet.repl :as pr]
    [dda.pallet.commons.session-tools :as session-tools]
    [dda.pallet.commons.pallet-schema :as ps]
    [dda.pallet.commons.operation :as operation]
    [dda.pallet.commons.existing :as existing]
    [dda.pallet.dda-managed-ide.app :as app]))

(defn apply-install
 [& options]
 (let [{:keys [domain targets]
        :or {domain "ide.edn"
             targets "targets.edn"}} options
       target-config (app/load-targets targets)
       domain-config (app/load-domain domain)]
   (operation/do-apply-install
     (app/existing-provider target-config)
     (app/existing-provisioning-spec domain-config target-config)
     :summarize-session true)))

(defn apply-configure
 [& options]
 (let [{:keys [domain targets]
        :or {domain "ide.edn"
             targets "targets.edn"}} options
       target-config (app/load-targets targets)
       domain-config (app/load-domain domain)]
   (operation/do-apply-configure
     (app/existing-provider target-config)
     (app/existing-provisioning-spec domain-config target-config)
     :summarize-session true)))

(defn serverspec
  [& options]
  (let [{:keys [domain targets]
         :or {domain "ide.edn"
              targets "targets.edn"}} options
        target-config (app/load-targets targets)
        domain-config (app/load-domain domain)]
    (operation/do-test
      (app/existing-provider target-config)
      (app/existing-provisioning-spec domain-config target-config)
      :summarize-session true)))
