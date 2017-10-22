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
   [dda.pallet.core.dda-crate :as dda-crate]
   [dda.pallet.dda-config-crate.infra :as config-crate]
   [dda.pallet.dda-git-crate.app :as git]
   [dda.pallet.dda-serverspec-crate.app :as serverspec]
   [dda.pallet.dda-managed-vm.app :as managed-vm]
   [dda.pallet.dda-managed-ide.infra :as infra]
   [dda.pallet.dda-managed-ide.domain :as domain]))

(def with-dda-ide infra/with-dda-ide)

(def InfraResult domain/InfraResult)

(def DdaIdeAppConfig
  {:group-specific-config
   {s/Keyword (merge InfraResult
                     git/InfraResult
                     serverspec/InfraResult
                     managed-vm/InfraResult)}})

(s/defn ^:allways-validate create-app-configuration :- DdaIdeAppConfig
  [config :- infra/DdaIdeConfig
   group-key :- s/Keyword]
  {:group-specific-config
   {group-key config}})

(defn app-configuration
  [domain-config & {:keys [group-key] :or {group-key :dda-ide-group}}]
  (s/validate domain/DdaIdeDomainConfig domain-config)
  (mu/deep-merge
   (managed-vm/app-configuration (domain/vm-config domain-config) :group-key group-key)
   (git/app-configuration (domain/ide-git-config domain-config) :group-key group-key)
   (serverspec/app-configuration (domain/ide-serverspec-config domain-config) :group-key group-key)
   (create-app-configuration (domain/infra-configuration domain-config) group-key)))

(s/defn ^:always-validate dda-ide-group-spec
  [app-config :- DdaIdeAppConfig]
  (group/group-spec
   app-config [(config-crate/with-config app-config)
               serverspec/with-serverspec
               git/with-git
               managed-vm/with-dda-vm
               with-dda-ide]))
