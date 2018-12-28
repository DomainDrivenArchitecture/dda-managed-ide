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
(ns dda.pallet.dda-managed-ide.infra.db
  (:require
    [clojure.string :as string]
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.actions :as actions]
    [dda.pallet.crate.util :as util]
    [dda.config.commons.user-home :as user-env]))

(def Dbvis
   {:version s/Str})

(def Db
  {(s/optional-key :dbvis) Dbvis})

(def Settings
   #{:install-pgtools})

(s/defn install-pgtools
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "-install system: install-pgtools")))
  (actions/packages
    :aptitude ["pgadmin3"]))

(s/defn
  install-dbvis
  "get and install dbvis at /opt/dbvis"
  [facility :- s/Keyword
   config :- Dbvis]
  (let [{:keys [version]} config]
    (actions/as-action
      (logging/info (str facility "-configure system: install-dbvis")))
    (actions/remote-directory
      "/opt/dbvis"
      :owner "root"
      :group "users"
      :recursive true
      :unpack :tar
      :url (str "http://www.dbvis.com/product_download/dbvis-" version
                "/media/dbvis_unix_" (string/replace version #"\." "_") ".tar.gz"))
    (actions/remote-file
      "/etc/profile.d/dbvis.sh"
      :literal true
      :content
      (util/create-file-content
        ["PATH=$PATH:/opt/dbvis"
         "export PATH"]))))

(s/defn install-system
  [facility :- s/Keyword
   ide-settings
   contains-db? :- s/Bool
   db :- Db]
  (let [{:keys [dbvis]} db]
    (when (contains? ide-settings :install-pgtools)
       (install-pgtools facility))
    (when contains-db?
      (when (contains? db :dbvis)
        (install-dbvis facility dbvis)))))
