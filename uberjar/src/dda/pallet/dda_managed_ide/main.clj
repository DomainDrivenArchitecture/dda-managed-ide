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
;[dda.pallet.commons.cli-helper :as cli-helper]

(ns dda.pallet.dda-managed-ide.main
  (:gen-class)
  (:require
    [clojure.string :as str]
    [clojure.tools.cli :as cli]
    [dda.pallet.commons.existing :as existing]
    [dda.pallet.commons.operation :as operation]
    [dda.pallet.dda-managed-ide.app :as app]))

(defn execute-server-test
  [domain-config targets]
  (let [{:keys [existing provisioning-user]} targets]
    (operation/do-server-test
     (existing/provider {:dda-managed-ide existing})
     (app/existing-provisioning-spec
       domain-config
       provisioning-user)
     :summarize-session true)))

(defn execute-install
  [domain-config targets]
  (let [{:keys [existing provisioning-user]} targets]
    (operation/do-apply-install
     (existing/provider {:dda-managed-ide existing})
     (app/existing-provisioning-spec
       domain-config
       provisioning-user)
     :summarize-session true)))

(defn execute-configure
  [domain-config targets]
  (let [{:keys [existing provisioning-user]} targets]
    (operation/do-apply-configure
     (existing/provider {:dda-managed-ide existing})
     (app/existing-provisioning-spec
       domain-config
       provisioning-user)
     :summarize-session true)))


(def localhost
  {:existing [{:node-name "test-ide1"
               :node-ip "127.0.0.1"}]})

(def cli-options
  [["-h" "--help"]
   ["-s" "--server-test"]
   ["-c" "--configure"]
   ["-t" "--targets TARGETS.edn" "edn file containing the targets to install on."
    :default localhost]])

(defn usage [options-summary]
  (str/join
   \newline
   ["dda-managed-ide installs a variety of standard software and configs on your personal desktop"
    ""
    "Usage: java -jar dda-managed-ide-[version]-standalone.jar [options] ide-spec-file"
    ""
    "Options:"
    options-summary
    ""
    "ide-spec-file"
    "  - follows the edn format."
    "  - has to be a valid DdaVmDomainConfig (see: https://github.com/DomainDrivenArchitecture/dda-managed-ide)"
    ""]))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))


(defn -main [& args]
  (let [{:keys [options arguments errors summary help]} (cli/parse-opts args cli-options)]
    (cond
      help (exit 0 (usage summary))
      errors (exit 1 (error-msg errors))
      (not= (count arguments) 1) (exit 1 (usage summary))
      (:server-test options) (execute-server-test
                                        (app/load-domain (first arguments))
                                        (if (= (options :targets) localhost)
                                          localhost
                                          (app/load-targets (:targets options))))
      (:configure options) (execute-configure
                             (app/load-domain (first arguments))
                             (if (= (options :targets) localhost)
                               localhost
                               (app/load-targets (:targets options))))
      :default (execute-install
                 (app/load-domain (first arguments))
                 (if (= (options :targets) localhost)
                   localhost
                   (app/load-targets (:targets options)))))))
