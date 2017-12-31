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
    [dda.config.commons.user-env :as user-env]
    [dda.pallet.dda-managed-ide.app :as app]
    [dda.pallet.dda-managed-ide.infra :as infra]))

(defn provisioning-spec [target-config domain-config]
  (let [{:keys [provisioning-user]} target-config]
    (merge
      (app/dda-ide-group-spec
        (app/app-configuration domain-config))
      (existing/node-spec provisioning-user))))

(defn provider [target-config]
  (let [{:keys [existing]} target-config]
    (existing/provider
     {infra/facility existing})))

(defn apply-install []
  (let [target-config (existing/load-targets "targets.edn")
        domain-config (app/load-domain "ide.edn")]
    (operation/do-apply-install
     (provider target-config)
     (provisioning-spec target-config domain-config)
     :summarize-session true)))

(defn apply-configure []
  (let [target-config (existing/load-targets "targets.edn")
        domain-config (app/load-domain "ide.edn")]
    (operation/do-apply-configure
     (provider target-config)
     (provisioning-spec target-config domain-config)
     :summarize-session true)))

(defn test []
  (let [target-config (existing/load-targets "targets.edn")
        domain-config (app/load-domain "ide.edn")]
    (operation/do-server-test
     (provider target-config)
     (provisioning-spec target-config domain-config)
     :summarize-session true)))
