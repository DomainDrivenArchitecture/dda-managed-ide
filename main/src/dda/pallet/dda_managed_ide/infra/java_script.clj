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

; # nodejs - mehrere Versionen
; curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.34.0/install.sh | bash
; export NVM_DIR="$HOME/.nvm"
; [ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
; [ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"  # This loads nvm bash_completion
; nvm install 6.16
; nvm install 8.15
; nvm install 9.11.2
; nvm install 10.15.0
; nvm install 11.7.0
; nvm install node
; nvm use 11.7.0
;
; apt install -y npm


(def NodeJs
   {:version s/Str}) ; 6.x, 8.x or 10.x works

(def JavaScript
   {(s/optional-key :nodejs) NodeJs})

(def Settings
   #{:install-yarn
     :install-npm
     :install-mach
     :install-asciinema})

(defn install-npm
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-npm")))
  (actions/packages
    :aptitude ["npm"]))

(s/defn
  init-nodejs
  [facility :- s/Keyword
   config :- NodeJs]
  (let [{:keys [version]} config]
    (actions/as-action
      (logging/info (str facility "-init system: init-nodejs")))
    (actions/package-source (str "nodejs_" version)
      :aptitude
      {:url (str "https://deb.nodesource.com/node_" version)
       :release "bionic"
       :scopes ["main"]
       :key-url "https://deb.nodesource.com/gpgkey/nodesource.gpg.key"})))

(s/defn
  install-nodejs
  "get and install install-nodejs"
  [facility :- s/Keyword
   config :- NodeJs]
  (let [{:keys [version]} config]
    (actions/as-action
      (logging/info (str facility "-install system: install-nodejs")))
    (actions/packages :aptitude ["nodejs"])))

(s/defn
  init-yarn
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "-init system: init-yarn")))
  (actions/package-source "yarn"
    :aptitude
    {:url "https://dl.yarnpkg.com/debian/"
     :release "stable"
     :scopes ["main"]
     :key-url "https://dl.yarnpkg.com/debian/pubkey.gpg"}))

(s/defn
  install-yarn
  "get and install install-yarn"
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "-install system: install-yarn")))
  (actions/packages :aptitude ["nodejs"]))

(s/defn init-asciinema
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "-init system: init-asciinema")))
  (actions/package-source "asciinema"
    :aptitude
    {:url "http://ppa.launchpad.net/zanchey/asciinema/ubuntu "
     :release "bionic"
     :scopes ["main"]
     :key-url "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x9D2E234C0F833EAD"}))

(s/defn install-asciinema
  [facility :- s/Keyword]
  (actions/as-action
    (logging/info (str facility "-install-system: install-asciinema")))
  (actions/packages :aptitude ["asciinema" "imagemagick" "gifsicle"])
  (actions/exec-checked-script
    "install asciicast2gif"
    ("npm" "install" "--global" "phantomjs-prebuilt" "--unsafe-perm=true" "--allow-root")
    ("npm" "install" "--global" "asciicast2gif" "--unsafe-perm=true" "--allow-root")))

(defn install-mach
  [facility]
  (actions/as-action
    (logging/info (str facility "-install system: install-mach")))
  (actions/exec-checked-script
    "install mach"
    ("npm" "install" "--global" "@juxt/mach" "--unsafe-perm=true" "--allow-root")
    ("cd" "/usr/local/bin")
    ("curl" "-fsSLo" "boot"
            "https://github.com/boot-clj/boot-bin/releases/download/latest/boot.sh")
    ("chmod" "755" "boot")))

(s/defn init-system
  [facility :- s/Keyword
   contains-java-script? :- s/Bool
   js :- JavaScript
   settings]
  (let [{:keys [nodejs]} js]
    (when contains-java-script?
      (when (contains? js :nodejs)
        (init-nodejs facility nodejs)))
    (when (contains? settings :install-yarn)
      (init-yarn facility))
    (when (contains? settings :install-asciinema)
       (init-asciinema facility))))

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
       (install-asciinema facility))
    (when (contains? settings :install-mach)
      (install-mach facility))))
