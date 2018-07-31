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

(ns dda.pallet.dda-managed-ide.infra.clojure
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.actions :as actions]
    [selmer.parser :as selmer]
    [dda.pallet.crate.util :as util]
    [dda.config.commons.user-home :as user-env]))


(def RepoAuth
  {:repo s/Str
   :username s/Str
   :password s/Str})

(def Clojure
  {(s/optional-key :signing-gpg-key) s/Str
   (s/optional-key :lein-auth) [RepoAuth]})

(def Settings
   #{})

(defn install-leiningen
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: clojure")))
  "get and install lein at /opt/leiningen"
  (actions/directory
    "/opt/leiningen"
    :owner "root"
    :group "users"
    :mode "755")
  (actions/remote-file
    "/opt/leiningen/lein"
    :owner "root"
    :group "users"
    :mode "755"
    :url "https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein")
  (actions/remote-file
    "/etc/profile.d/lein.sh"
    :literal true
    :content
    (util/create-file-content
      ["PATH=$PATH:/opt/leiningen"])))

(s/defn lein-user-profile
  [lein-config :- Clojure]
  (selmer/render-file "lein_profiles.template" lein-config))

(s/defn configure-user-leiningen
  "configure lein settings"
  [facility :- s/Keyword
   os-user-name :- s/Str
   lein-config :- Clojure]
  (let [path (str (user-env/user-home-dir os-user-name) "/.lein/")]
    (actions/as-action
      (logging/info (str facility "-configure user: clojure")))
    (actions/directory
      path
      :owner os-user-name
      :group os-user-name
      :mode "755")
    (actions/remote-file
      (str path "profiles.clj")
      :owner os-user-name
      :group os-user-name
      :literal true
      :content
      (lein-user-profile lein-config))))


(s/defn install-system
  [facility :- s/Keyword
   contains-clojure? :- s/Bool
   clojure :- Clojure]
  (when contains-clojure?
    (install-leiningen facility)))

(s/defn configure-user
  [facility :- s/Keyword
   os-user-name :- s/Str
   contains-clojure? :- s/Bool
   clojure :- Clojure]
  (when contains-clojure?
    (configure-user-leiningen facility os-user-name clojure)))
