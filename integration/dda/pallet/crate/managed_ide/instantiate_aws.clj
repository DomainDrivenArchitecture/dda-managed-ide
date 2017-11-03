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
(ns dda.pallet.crate.managed-ide.instantiate-aws
  (:require
    [clojure.inspector :as inspector]
    [schema.core :as s]
    [pallet.api :as api]
    [dda.config.commons.map-utils :as mu]
    [pallet.compute :as compute]
    [dda.pallet.commons.encrypted-credentials :as crypto]
    [dda.pallet.commons.session-tools :as session-tools]
    [dda.pallet.commons.pallet-schema :as ps]
    [dda.cm.operation :as operation]
    [dda.cm.aws :as cloud-target]
    [dda.pallet.dda-config-crate.infra :as config-crate]
    [dda.pallet.dda-git-crate.infra :as git-crate]
    [dda.pallet.dda-serverspec-crate.domain :as server-test-domain]
    [dda.pallet.dda-serverspec-crate.infra :as server-test-crate]
    [dda.pallet.dda-git-crate.domain :as domain]
    [dda.pallet.domain.managed-ide.config :as ide-config]))

(def git-config
  {:os-user :ubuntu
   :user-email "ubuntu@domain"
   :repo-groups #{:dda-pallet}})

(def test-config
  {:file {:ubuntu-code {:path "/home/ubuntu/code"
                        :exist? true}}})

(defn group [stack-config]
  (let []
   (api/group-spec
     "dda-managed-ide-group"
     :extends [(config-crate/with-config stack-config)
               user-crate/with-user
               git-crate/with-git
               managed-vm/with-dda-vm
               managed-ide/with-dda-ide
               server-test-crate/with-servertest])))

(defn integrated-group-spec [count]
  (merge
    (group (group-configuration))
    (cloud-target/node-spec "jem")
    {:count count}))

(defn converge-install
  ([count]
   (operation/do-converge-install (cloud-target/provider) (integrated-group-spec count)))
  ([key-id key-passphrase count]
   (operation/do-converge-install (cloud-target/provider key-id key-passphrase) (integrated-group-spec count))))

(defn server-test
  ([count]
   (operation/do-server-test (cloud-target/provider) (integrated-group-spec count)))
  ([key-id key-passphrase count]
   (operation/do-server-test (cloud-target/provider key-id key-passphrase) (integrated-group-spec count))))
