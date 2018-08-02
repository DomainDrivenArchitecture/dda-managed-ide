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

(ns dda.pallet.dda-managed-ide.infra.java-script
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.actions :as actions]
    [selmer.parser :as selmer]
    [dda.pallet.crate.util :as util]
    [dda.config.commons.user-home :as user-env]))

(def NodeJs
   {:version s/Str}) ; 6.x, 8.x or 10.x works

(def JavaScript
   {(s/optional-key :nodejs) NodeJs})

(def Settings
   #{:install-yarn
     :install-npm
     :install-asciinema})

(defn install-npm
  [facility]
  (actions/as-action
    (logging/info (str facility " install system: install-npm")))
  (actions/packages
    :aptitude ["npm"]))

(s/defn
  install-nodejs
  "get and install install-nodejs"
  [facility :- s/Keyword
   config :- NodeJs]
  (let [{:keys [version]} config]
    (actions/as-action
      (logging/info (str facility " install system: install-nodejs")))
    (actions/package-source (str "nodejs_" version)
      :aptitude
      {:url (str "https://deb.nodesource.com/node_" version)
       :release "bionic"
       :scopes ["main"]
       :key-url "https://deb.nodesource.com/gpgkey/nodesource.gpg.key"})
    (actions/package-manager :update)
    (actions/packages :aptitude ["nodejs"])))

(s/defn
  install-yarn
  "get and install install-yarn"
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility " install system: install-yarn")))
  (actions/package-source "yarn"
    :aptitude
    {:url "https://dl.yarnpkg.com/debian/"
     :release "stable"
     :scopes ["main"]
     :key-url "https://dl.yarnpkg.com/debian/pubkey.gpg"})
  (actions/package-manager :update)
  (actions/packages :aptitude ["nodejs"]))

(s/defn install-asciinema
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "configure system: install-asciinema")))
  (actions/package-source "asciinema"
    :aptitude
    {:url "http://ppa.launchpad.net/zanchey/asciinema/ubuntu "
     :release "bionic"
     :scopes ["main"]
     :key-url "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x9D2E234C0F833EAD"})
  (actions/package-manager :update)
  (actions/packages :aptitude ["asciinema" "phantomjs" "imagemagick" "gifsicle"])
  (actions/exec-checked-script
    "install asciicast2gif"
    ("npm" "install" "--global" "asciicast2gif")))


(s/defn install-system
  [facility :- s/Keyword
   contains-java-script? :- s/Bool
   js :- JavaScript
   settings]
  (let [{:keys [nodejs]} js]
    (when contains-java-script?
      (when (contains? js :nodejs)
        (install-nodejs facility nodejs)))
    (when (contains? settings :install-npm)
      (install-npm facility))
    (when (contains? settings :install-yarn)
      (install-yarn facility))
    (when (contains? settings :install-asciinema)
       (install-asciinema facility))))
