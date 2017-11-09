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
   [dda.config.commons.map-utils :as mu]
   [dda.pallet.commons.external-config :as ext-config]
   [dda.pallet.dda-config-crate.infra :as config-crate]
   [dda.pallet.dda-git-crate.app :as git]
   [dda.pallet.dda-user-crate.app :as user]
   [dda.pallet.dda-serverspec-crate.app :as serverspec]
   [dda.pallet.dda-managed-vm.app :as managed-vm]
   [dda.pallet.dda-managed-ide.infra :as infra]
   [dda.pallet.dda-managed-ide.domain :as domain]))

(def with-dda-ide infra/with-dda-ide)

(def InfraResult domain/InfraResult)

(def DdaIdeAppConfig
  {:group-specific-config
   {s/Keyword (merge ;InfraResult
                     managed-vm/InfraResult
                     git/InfraResult
                     user/InfraResult
                     serverspec/InfraResult)}})

(s/defn ^:always-validate load-domain :- domain/DdaIdeDomainConfig
  [file-name :- s/Str]
  (ext-config/parse-config file-name))

(s/defn ^:always-validate app-configuration :- DdaIdeAppConfig
  [domain-config :- domain/DdaIdeDomainConfig
   & options]
  (let [{:keys [group-key] :or {group-key infra/facility}} options]
    (s/validate domain/DdaIdeDomainConfig domain-config)
    (mu/deep-merge
     (managed-vm/app-configuration (domain/dda-vm-domain-configuration domain-config) :group-key group-key))))
     ;(git/app-configuration (domain/ide-git-config domain-config) :group-key group-key)
     ;(serverspec/app-configuration (domain/ide-serverspec-config domain-config) :group-key group-key)
     ;{:group-specific-config {group-key (domain/infra-configuration domain-config)})))

(s/defn ^:always-validate dda-ide-group-spec
  [app-config :- DdaIdeAppConfig]
  (group/group-spec
   app-config [(config-crate/with-config app-config)
               serverspec/with-serverspec
               user/with-user
               git/with-git
               managed-vm/with-dda-vm]))
               ;with-dda-ide]))
