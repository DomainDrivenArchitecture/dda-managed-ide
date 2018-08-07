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

(ns dda.pallet.dda-managed-ide.infra.java
  (:require
    [clojure.tools.logging :as logging]
    [schema.core :as s]
    [pallet.actions :as actions]
    [selmer.parser :as selmer]
    [dda.pallet.crate.util :as util]
    [dda.config.commons.user-home :as user-env]))

(def CustomJava
   {:tar-download-url s/Str
    :jdk-filesystem-name s/Str
    (s/optional-key :md5-hash) s/Str})

(def Gradle
   {:version s/Str})

(def Java
  {(s/optional-key :java-default-to) s/Str
   (s/optional-key :custom-java) CustomJava
   (s/optional-key :gradle) Gradle})

(def Settings
   #{})

(s/defn
  install-custom-java
  "get and install java at /opt/java"
  [facility :- s/Keyword
   config :- CustomJava]
  (let [{:keys [tar-download-url jdk-filesystem-name md5-hash]} config]
    (actions/as-action
      (logging/info (str facility "-install system: install-custom-java")))
    (actions/directory "/opt/java"
      :owner "root"
      :group "root"
      :mode "755")
    (if (contains? config :md5-hash)
      (actions/remote-directory
        "/opt/java"
        :owner "root"
        :group "users"
        :mode "755"
        :unpack :tar
        :nd5 md5-hash
        :url tar-download-url)
      (actions/remote-directory
        "/opt/java"
        :owner "root"
        :group "users"
        :mode "755"
        :unpack :tar
        :url tar-download-url))
    (actions/remote-file
      "/etc/profile.d/java.sh"
      :literal true
      :content
      (util/create-file-content
        [(str "export JAVA_HOME=/opt/java/" jdk-filesystem-name)
         "PATH=$JAVA_HOME/bin:$PATH"
         "export PATH"]))))

(s/defn
  configure-java-default-to
  [facility :- s/Keyword
   java-default-to :- s/Str]
  (actions/as-action
    (logging/info (str facility "-configure system: configure-java-default-to")))
  (actions/exec-checked-script
    "configure-java-default-to"
    ("update-alternatives" "--set" "java" ~java-default-to)))

(s/defn
  install-gradle
  "get and install gradle at /opt/gradle"
  [facility :- s/Keyword
   config :- Gradle]
  (let [{:keys [version]} config]
    (actions/as-action
      (logging/info (str facility "-install system: install-gradle")))
    (actions/remote-directory
      "/opt/gradle"
      :owner "root"
      :group "users"
      :recursive true
      :unpack :unzip
      :url (str "https://downloads.gradle.org/distributions/gradle-" version "-bin.zip"))
    (actions/remote-file
      "/etc/profile.d/gradle.sh"
      :literal true
      :content
      (util/create-file-content
        [(str "export GRADLE_HOME=/opt/gradle/gradle-" version)
         "PATH=$PATH:$GRADLE_HOME/bin"
         "export PATH"]))))

(s/defn configure-system
  [facility :- s/Keyword
   contains-java? :- s/Bool
   java :- Java]
  (let [{:keys [java-default-to]} java]
    (when contains-java?
      (when (contains? java :java-default-to)
        (configure-java-default-to facility java-default-to)))))

(s/defn install-system
  [facility :- s/Keyword
   contains-java? :- s/Bool
   java :- Java]
  (let [{:keys [custom-java gradle]} java]
    (when contains-java?
      (when (contains? java :custom-java)
        (install-custom-java facility custom-java))
      (when (contains? java :gradle)
        (install-gradle facility gradle)))))
