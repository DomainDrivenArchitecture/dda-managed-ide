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
(ns dda.pallet.dda-managed-ide.app.instantiate-aws
  (:require
    [clojure.inspector :as inspector]
    [schema.core :as s]
    [pallet.api :as api]
    [dda.pallet.commons.operation :as operation]
    [dda.pallet.commons.aws :as cloud-target]
    [dda.pallet.dda-managed-ide.app :as app]))

(defn provisioning-spec [domain-config target-config count]
  (merge
    (app/dda-ide-group-spec
      (app/app-configuration domain-config))
    (cloud-target/node-spec target-config)
    {:count count}))

(defn converge-install
  [count & options]
  (let [{:keys [gpg-key-id gpg-passphrase domain targets
                summarize-session]
         :or {domain "integration/resources/snakeoil-ide-remote.edn"
              targets "integration/resources/user-aws-target.edn"
              summarize-session true}} options
        target-config (cloud-target/load-targets targets)
        domain-config (app/load-domain domain)]
   (operation/do-converge-install
     (cloud-target/provider (:context target-config))
     (provisioning-spec domain-config (:node-spec target-config) count)
     :summarize-session summarize-session)))

(defn configure
 [& options]
 (let [{:keys [gpg-key-id gpg-passphrase domain targets
               summarize-session]
        :or {domain "integration/resources/snakeoil-ide-remote.edn"
             targets "integration/resources/user-aws-target.edn"
             summarize-session true}} options
       target-config (cloud-target/load-targets targets)
       domain-config (app/load-domain domain)]
  (operation/do-apply-configure
    (cloud-target/provider (:context target-config))
    (provisioning-spec domain-config (:node-spec target-config) 0)
    :summarize-session summarize-session)))

(defn serverspec
  [& options]
  (let [{:keys [gpg-key-id gpg-passphrase domain targets
                summarize-session]
         :or {domain "integration/resources/snakeoil-ide-remote.edn"
              targets "integration/resources/user-aws-target.edn"
              summarize-session true}} options
        target-config (cloud-target/load-targets targets)
        domain-config (app/load-domain domain)]
    (operation/do-server-test
      (cloud-target/provider (:context target-config))
      (provisioning-spec domain-config (:node-spec target-config) 0)
      :summarize-session summarize-session)))
