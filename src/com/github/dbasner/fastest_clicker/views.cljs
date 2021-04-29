(ns com.github.dbasner.fastest-clicker.views
  (:require
   [re-frame.core :as re-frame]
   [com.github.dbasner.fastest-clicker.subs :as subs]
   [com.github.dbasner.fastest-clicker.components.button :refer [ButtonHolder]]))


(defn main-panel []
    [:div
     [ButtonHolder]])
