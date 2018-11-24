(defproject dda/dda-managed-ide "2.0.5-SNAPSHOT"
  :description "module to install and configure ide based on ubuntu vm."
  :url "https://www.domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[dda/dda-pallet "2.2.1"]
                 [dda/dda-managed-vm "2.2.3"]]
  :source-paths ["main/src"]
  :resource-paths ["main/resources"]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :profiles {:dev {:source-paths ["integration/src"
                                  "test/src"
                                  "uberjar/src"]
                   :resource-paths ["integration/resources"
                                    "test/resources"]
                   :dependencies
                   [[org.clojure/test.check "0.10.0-alpha3"]
                    [dda/pallet "0.9.0" :classifier "tests"]
                    [ch.qos.logback/logback-classic "1.3.0-alpha4"]
                    [org.slf4j/jcl-over-slf4j "1.8.0-beta2"]]
                   :plugins
                   [[lein-sub "0.3.0"]]
                   :leiningen/reply
                   {:dependencies [[org.slf4j/jcl-over-slf4j "1.8.0-beta0"]]
                    :exclusions [commons-logging]}
                   :repl-options {:init-ns dda.pallet.dda-managed-ide.app.instantiate-existing}}
             :test {:test-paths ["test/src"]
                    :resource-paths ["test/resources"]
                    :dependencies [[dda/pallet "0.9.0" :classifier "tests"]]}
             :uberjar {:source-paths ["uberjar/src"]
                       :resource-paths ["uberjar/resources"]
                       :aot :all
                       :main dda.pallet.dda-managed-ide.main
                       :dependencies [[org.clojure/tools.cli "0.4.1"]
                                      [ch.qos.logback/logback-classic "1.3.0-alpha4"]
                                      [org.slf4j/jcl-over-slf4j "1.8.0-beta2"]]}}
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy"]
                  ["uberjar"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]
  :local-repo-classpath true)
