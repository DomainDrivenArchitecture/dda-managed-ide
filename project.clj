(defproject org.domaindrivenarchitecture/dda-managed-ide "0.1.0-SNAPSHOT"
  :description "module to install and configure ide based on ubuntu vm."
  :url "https://www.domaindrivenarchitecture.org"
  :pallet {:source-paths ["src"]}
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.palletops/pallet "0.8.12"]
                 [com.palletops/stevedore "0.8.0-beta.7"]
                 [org.domaindrivenarchitecture/dda-pallet-commons "0.2.2-SNAPSHOT"]
                 [org.domaindrivenarchitecture/dda-pallet "0.2.1-SNAPSHOT"]
                 [org.domaindrivenarchitecture/dda-managed-vm "0.1.2-SNAPSHOT"]]
  :profiles {:dev
             {:source-paths ["integration"]
              :resource-paths ["dev-resources"]
              :dependencies
              [[com.palletops/pallet "0.8.12" :classifier "tests"]
               [org.domaindrivenarchitecture/dda-pallet-commons "0.2.2-SNAPSHOT" :classifier "tests"]
               [org.domaindrivenarchitecture/pallet-aws "0.2.8-SNAPSHOT"]
               [ch.qos.logback/logback-classic "1.1.7"]
               [org.slf4j/jcl-over-slf4j "1.7.21"]]
              :plugins
              [[com.palletops/pallet-lein "0.8.0-alpha.1"]
               [lein-sub "0.3.0"]]}
              :leiningen/reply
               {:dependencies [[org.slf4j/jcl-over-slf4j "1.7.21"]]
                :exclusions [commons-logging]}}
   :local-repo-classpath true
   :repositories [["snapshots" :clojars]
                  ["releases" :clojars]]
   :deploy-repositories [["snapshots" :clojars]
                         ["releases" :clojars]]
   :classifiers {:tests {:source-paths ^:replace ["test" "integration"]
                         :resource-paths ^:replace ["dev-resources"]}})
