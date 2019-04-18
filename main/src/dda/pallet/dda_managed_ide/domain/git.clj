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

(ns dda.pallet.dda-managed-ide.domain.git
  (:require
    [schema.core :as s]
    [dda.config.commons.map-utils :as mu]
    [dda.pallet.commons.secret :as secret]
    [dda.pallet.dda-git-crate.domain :as git-domain]
    [dda.pallet.dda-managed-vm.domain.git :as git-repo]))

(def ServerIdentity git-domain/ServerIdentity)
(def Repository git-domain/Repository)
(def Repositories [Repository])
(def GitCredential git-domain/GitCredential)
(def GitCredentials git-domain/GitCredentials)
(def GitCredentialsResolved git-domain/GitCredentialsResolved)

(def repo-names
  ["dda-config-commons", "dda-pallet-commons" ,"dda-pallet","dda-user-crate", "dda-backup-crate" ,"dda-git-crate",
   "dda-hardening-crate", "httpd-crate", "dda-httpd-crate", "dda-liferay-crate", "dda-managed-vm", "dda-managed-ide",
   "dda-mariadb-crate", "dda-serverspec-crate", "dda-tomcat-crate", "dda-cloudspec"])

(s/defn ide-git-config
 [name :- s/Str
  email :- s/Str
  git-credentials :- GitCredentials
  desktop-wiki :- Repositories
  credential-store :- Repositories]
 (let [email (if (some? email) email (str name "@mydomain"))
       protocol-type (git-repo/github-protocol-type git-credentials)]
   {(keyword name)
    (merge
      {:user-email email}
      (when (some? git-credentials)
        {:credential git-credentials})
      {:repo {:books
              [{:host "github.com"
                :orga-path "DomainDrivenArchitecture"
                :repo-name "ddaArchitecture"
                :protocol protocol-type
                :server-type :github}]
              :dda-pallet
              (into []
               (for [repo-name repo-names]
                 (merge {:repo-name repo-name}
                        {:host "github.com"
                          :orga-path "DomainDrivenArchitecture"
                          :protocol protocol-type
                          :server-type :github})))}}
      {:synced-repo
       (merge
         {:credential-store (git-repo/credential-store-setup credential-store protocol-type)}
         (when (some? desktop-wiki)
          {:desktop-wiki desktop-wiki}))}
      {})}))
