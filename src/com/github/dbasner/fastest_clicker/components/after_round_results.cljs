(ns com.github.dbasner.fastest-clicker.components.after-round-results
  (:require [re-frame.core :as rf]))
;;TODO CREATE COMPONENT FOR END_OF_FIRST ROUND
;; "total/breakdown" times and then player 2 start

;;-----------------------------------------------------------------------------
;; UTIL

(defn round-length-seconds
  [times]
  (- (last times) (first times)))
;;-----------------------------------------------------------------------------
;; EVENTS


;;-----------------------------------------------------------------------------
;; SUBSCRIPTIONS

(rf/reg-sub
  :round-1-total-time
  :<- [:times]
  (fn [times _]
    (round-length-seconds times)))


;;show the results if
;; round-1 finished and round 2 not finished
(rf/reg-sub
  :show-round-1-results
  (fn [db query]
    ()))








;;-----------------------------------------------------------------------------
;; VIEWS

(defn RoundOneResultCard []
  (let [timestamps @(rf/subscribe [:round-1-total-time])]))



