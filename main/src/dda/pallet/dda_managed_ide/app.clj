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
   [dda.pallet.commons.secret :as secret]
   [dda.pallet.commons.existing :as existing]
   [dda.pallet.commons.external-config :as ext-config]
   [dda.pallet.dda-config-crate.infra :as config-crate]
   [dda.pallet.dda-git-crate.app :as git]
   [dda.pallet.dda-user-crate.app :as user]
   [dda.pallet.dda-serverspec-crate.app :as serverspec]
   [dda.pallet.dda-managed-vm.app :as managed-vm]
   [dda.pallet.dda-managed-ide.infra :as infra]
   [dda.pallet.dda-managed-ide.domain :as domain]))

(def with-dda-ide infra/with-dda-ide)

(def DdaIdeDomainConfig domain/DdaIdeDomainConfig)

(def DdaIdeDomainResolvedConfig domain/DdaIdeDomainResolvedConfig)

(def InfraResult domain/InfraResult)

(def ProvisioningUser existing/ProvisioningUser)

(def Targets existing/Targets)

(def DdaIdeAppConfig
  {:group-specific-config
   {s/Keyword (merge InfraResult
                     managed-vm/InfraResult
                     git/InfraResult
                     user/InfraResult
                     serverspec/InfraResult)}})

(s/defn ^:always-validate load-targets :- Targets
  [file-name :- s/Str]
  (ext-config/parse-config file-name))

(s/defn ^:always-validate load-domain :- DdaIdeDomainConfig
  [file-name :- s/Str]
  (ext-config/parse-config file-name))

(s/defn ^:always-validate
  resolve-repo-auth-secrets :- domain/RepoAuthResolved
  [domain-config :- domain/RepoAuth]
  (let [{:keys [username password]} domain-config]
    (merge
      domain-config
      {:username (secret/resolve-secret (:username domain-config))
       :password (secret/resolve-secret (:password domain-config))})))

(s/defn ^:always-validate resolve-secrets :- DdaIdeDomainResolvedConfig
  [domain-config :- DdaIdeDomainConfig]
  (let [{:keys [user type lein-auth]} domain-config
        {:keys [ssh gpg]} user]
    (merge
      domain-config
      {:user (merge
               user
               {:password (secret/resolve-secret (:password user))}
               (when (contains? user :ssh)
                {:ssh {:ssh-public-key (secret/resolve-secret (:ssh-public-key ssh))
                       :ssh-private-key (secret/resolve-secret (:ssh-private-key ssh))}})
               (when (contains? user :gpg)
                {:gpg {:gpg-public-key (secret/resolve-secret (:gpg-public-key gpg))
                       :gpg-private-key (secret/resolve-secret (:gpg-private-key gpg))
                       :gpg-passphrase (secret/resolve-secret (:gpg-passphrase gpg))}}))}
      (when (contains? domain-config :lein-auth)
        {:lein-auth (into [] (map resolve-repo-auth-secrets lein-auth))}))))

(s/defn ^:always-validate app-configuration :- DdaIdeAppConfig
  [domain-config :- DdaIdeDomainResolvedConfig
   & options]
  (let [{:keys [group-key] :or {group-key infra/facility}} options]
    (s/validate DdaIdeDomainConfig domain-config)
    (mu/deep-merge
     (managed-vm/app-configuration-resolved (domain/dda-vm-domain-configuration domain-config) :group-key group-key)
     (git/app-configuration (domain/ide-git-config domain-config) :group-key group-key)
     (serverspec/app-configuration (domain/ide-serverspec-config domain-config) :group-key group-key)
     {:group-specific-config {group-key (domain/infra-configuration domain-config)}})))

(s/defn ^:always-validate dda-ide-group-spec
  [app-config :- DdaIdeAppConfig]
  (group/group-spec
   app-config [(config-crate/with-config app-config)
               serverspec/with-serverspec
               user/with-user
               git/with-git
               managed-vm/with-dda-vm
               with-dda-ide]))

(s/defn ^:always-validate existing-provisioning-spec
  "Creates an integrated group spec from a domain config and a provisioning user."
  [domain-config :- DdaIdeDomainConfig
   provisioning-user :- ProvisioningUser]
  (merge
   (dda-ide-group-spec (app-configuration domain-config))
   (existing/node-spec provisioning-user)))
