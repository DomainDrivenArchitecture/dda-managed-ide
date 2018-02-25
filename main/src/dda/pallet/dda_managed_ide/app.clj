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

(ns dda.pallet.dda-managed-ide.app
  (:require
   [schema.core :as s]
   [dda.cm.group :as group]
   [dda.pallet.core.app :refer :all]
   [dda.config.commons.map-utils :as mu]
   [dda.pallet.commons.secret :as secret]
   [dda.pallet.commons.existing :as existing]
   [dda.pallet.dda-config-crate.infra :as config-crate]
   [dda.pallet.dda-git-crate.app :as git]
   [dda.pallet.dda-user-crate.app :as user]
   [dda.pallet.dda-serverspec-crate.app :as serverspec]
   [dda.pallet.dda-managed-vm.app :as managed-vm]
   [dda.pallet.dda-managed-ide.infra :as infra]
   [dda.pallet.dda-managed-ide.domain :as domain]
   [dda.pallet.commons.external-config :as ext-config]))

(def with-dda-ide infra/with-dda-ide)

(def DdaIdeDomainConfig domain/DdaIdeDomainConfig)

(def DdaIdeDomainResolvedConfig domain/DdaIdeDomainResolvedConfig)

(def InfraResult domain/InfraResult)

(def DdaIdeAppConfig
  {:group-specific-config
   {s/Keyword (merge InfraResult
                     managed-vm/InfraResult
                     git/InfraResult
                     user/InfraResult
                     serverspec/InfraResult)}})

(s/defn ^:always-validate
  app-configuration-resolved :- DdaIdeAppConfig
  [resolved-domain-config :- DdaIdeDomainResolvedConfig
   & options]
  (let [{:keys [group-key] :or {group-key infra/facility}} options
        {:keys [type]} resolved-domain-config]
    (mu/deep-merge
     (managed-vm/app-configuration-resolved (domain/dda-vm-domain-configuration resolved-domain-config) :group-key group-key)
     (git/app-configuration (domain/ide-git-config resolved-domain-config) :group-key group-key)
     (serverspec/app-configuration (domain/ide-serverspec-config resolved-domain-config) :group-key group-key)
     {:group-specific-config {group-key (domain/infra-configuration resolved-domain-config)}})))

(s/defn ^:always-validate
  app-configuration :- DdaIdeAppConfig
  [domain-config :- DdaIdeDomainConfig
   & options]
  (let [resolved-domain-config (secret/resolve-secrets domain-config DdaIdeDomainConfig)]
    (apply app-configuration-resolved resolved-domain-config options)))

(s/defmethod group-spec :dda-managed-ide
  [crate-app  domain-config]
  (let [app-config (app-configuration-resolved domain-config)]
    (group/group-spec
     app-config [(config-crate/with-config app-config)
                 serverspec/with-serverspec
                 user/with-user
                 git/with-git
                 managed-vm/with-dda-vm
                 with-dda-ide])))

(def crate-app (make-dda-crate-app
                  :facility :dda-managed-ide
                  :domain-schema DdaIdeDomainConfig
                  :domain-schema-resolved DdaIdeDomainResolvedConfig
                  :default-domain-file "ide.edn"))
