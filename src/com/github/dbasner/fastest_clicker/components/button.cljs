(ns com.github.dbasner.fastest-clicker.components.button
  (:require [re-frame.core :as rf]
            [clojure.set :as set]))

;;-----------------------------------------------------------------------------
;; UTIL

(defn add-timestamp
  [db timestamp]
  (update db :click-times conj timestamp))

(defn mark-as-clicked
  [db btn-pos]
  (let [db-button-clicked (update db :current-clicked-buttons conj btn-pos)]
    db-button-clicked))

(defn pair->diff
  [timestamp-pair]
  (let [earlier-time (first timestamp-pair)
        later-time (second timestamp-pair)]
    {:time earlier-time :diff (- later-time earlier-time)}))

(defn log-and-pass-through [db message]
  (println message)
  (cljs.pprint/pprint db)
  db)

(defn timestamps->diffs
  [times]
  (let [time-pairs (partition 2 1 times)]
    (map pair->diff time-pairs)))

(defn get-current-button-set
  [db]
  (let [button-sets (:button-sets db)
        curr-set-idx (:curr-set-idx db)]
    (nth button-sets curr-set-idx)))

;; what is happening here
;; the set of buttons without the clicked ones is the ones that have not been clicked
;; aka unclicked
(defn get-visible-buttons
  [db]
  (let [buttons (get-current-button-set db)
        clicked-buttons (:current-clicked-buttons db)
        diff (set/difference buttons clicked-buttons)]
    diff))

(defn clicked-all-buttons?
  [db]
  (let [visible-buttons (get-visible-buttons db)]
    (empty? visible-buttons)))

(defn show-next-button-set
  [db]
  (if (clicked-all-buttons? db)
    (-> db
        (update :current-clicked-buttons empty)
        (update :curr-set-idx inc))
    db))

(defn is-final-button-set? [db]
  (let [finished-sets (inc (:curr-set-idx db))
        set-count (count (:button-sets db))]
    (println "finished sets" finished-sets)
    (println "set count" set-count)
    (not (< finished-sets set-count))))

(defn reset-set-index
  [db]
  (assoc db :curr-set-idx 0))

(defn mark-player-1-done
  [db]
  (assoc db :player-1-done true))

(defn mark-player-2-done
  [db]
  (assoc db :player-2-done true))

(defn end-round
  [db]
  (if (is-final-button-set? db)
    (-> db
        (reset-set-index)
        (mark-player-1-done))
    db))

;;home page on init w/ start button
;; game starts, button sequences randomized
;; increment until end of thing for player 1
;; player-x-done becomes true
;; when ^^^done show end-of-round-results and player 2 start
;; same thing
;; player-2-done
;; when done show result page, replay button


;;-----------------------------------------------------------------------------
;; EVENTS


;;TODO refactor this to an effect because .now is not a pure function (maybe "effect" in component is ok?

(rf/reg-event-db
  ::click-button
  (fn [db [_event btn-pos timestamp]]
    (-> db
        (add-timestamp timestamp)
        (mark-as-clicked btn-pos)
        (show-next-button-set)
        (end-round))))


;;-----------------------------------------------------------------------------
;; SUBSCRIPTIONS

(rf/reg-sub
  :times
  (fn [db _]
    (:click-times db)))

(rf/reg-sub
  ::times-with-diffs
  :<- [:times]
  (fn [times _]
    (timestamps->diffs times)))

(rf/reg-sub
  ::visible-buttons
  (fn [db _]
    (get-visible-buttons db)))


;;-----------------------------------------------------------------------------
;; VIEWS


(defn IndivTime [time]
  ^{:key time}[:li (str "time: " (:time time) " diff: " (:diff time))])

(defn TimeList [timestamps]
  [:div
   [:ul (map IndivTime timestamps)]])

(defn Button [btn-txt x y visible?]
  (let [class-str "bg-blue-500 hover:bg-blue-400 text-white font-bold py-2 px-4 border-b-4 border-blue-700 hover:border-blue-500 rounded"
        properties (if visible?
                     {:class class-str :on-click #(rf/dispatch [::click-button [x y] (.now js/Date)])}
                     {:class class-str :style {:visibility "hidden"}})]
    [:button properties  btn-txt]))

(defn ButtonGrid
  [x y visible-buttons]
  (let [cols (range x)
        rows (range y)]
    [:div {:class ["grid" (str "grid-cols-" x) "gap-4"]}
     (for [row rows
           col cols]
       (let [visible? (contains? visible-buttons [col row])
             btn-txt (str "btn " col " " row)]
         [:div {:key btn-txt} [Button btn-txt col row visible?]]))]))


(defn ButtonHolder []
  (let [times @(rf/subscribe [::times-with-diffs])
        visible-buttons @(rf/subscribe [::visible-buttons])]
    [:div
     [ButtonGrid 10 10 visible-buttons]
     [TimeList times]]))
