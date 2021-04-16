(ns com.github.dbasner.fastest-clicker.views
  (:require
   [re-frame.core :as re-frame]
   [com.github.dbasner.fastest-clicker.subs :as subs]))


(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      "Hello from " @name]]))

