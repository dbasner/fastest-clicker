(ns  com.github.dbasner.fastest-clicker.db)

(def default-db
  {:button-sets [#{[0 0] [1 1]} #{[5 5] [3 9]}]
   :current-clicked-buttons #{}
   :player-1-done false
   :player-2-done false
   :curr-set-idx 0
   :click-times []})