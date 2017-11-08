(defproject dda/dda-managed-ide "0.1.3-SNAPSHOT"
  :description "module to install and configure ide based on ubuntu vm."
  :url "https://www.domaindrivenarchitecture.org"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [dda/dda-pallet-commons "0.5.2"]
                 [dda/dda-pallet "0.6.2"]
                 [dda/dda-user-crate "0.7.0"]
                 [dda/dda-serverspec-crate "0.2.2"]
                 [dda/dda-git-crate "0.1.4-SNAPSHOT"]
                 [dda/dda-backup-crate "0.6.2-SNAPSHOT"]
                 [dda/dda-managed-vm "0.5.0-SNAPSHOT"]]
  :repositories [["snapshots" :clojars]
                 ["releases" :clojars]]
  :deploy-repositories [["snapshots" :clojars]
                        ["releases" :clojars]]
  :profiles {:uberjar
             {:aot :all
              :main main}
             :dev
             {:source-paths ["integration"]
              :resource-paths ["dev-resources"]
              :dependencies
              [[org.clojure/test.check "0.10.0-alpha2"]
               [org.domaindrivenarchitecture/pallet-aws "0.2.8.2"]
               [com.palletops/pallet "0.8.12" :classifier "tests"]
               [org.domaindrivenarchitecture/dda-pallet-commons "0.3.2" :classifier "tests"]
               [ch.qos.logback/logback-classic "1.2.3"]
               [org.slf4j/jcl-over-slf4j "1.8.0-beta0"]]
              :plugins
              [[com.palletops/pallet-lein "0.8.0-alpha.1"]
               [lein-sub "0.3.0"]]}
             :leiningen/reply
             {:dependencies [[org.slf4j/jcl-over-slf4j "1.8.0-beta0"]]
              :exclusions [commons-logging]}}
  :local-repo-classpath true
  :classifiers {:tests {:source-paths ^:replace ["test" "integration"]
                        :resource-paths ^:replace ["dev-resources"]}})
