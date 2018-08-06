(ns dda.pallet.dda-managed-ide.domain.idea
  (:require [schema.core :as s]))

(defn idea-config
  [vm-type contains-clojure? contains-devops?]
  {:plugins [{:plugin-name "cursive" :plugin-config {:license s/Str}}]})

(defn pycharm-config
  []
  {:plugins []})
