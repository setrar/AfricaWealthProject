(use '(incanter core stats charts datasets))

  (def cars (get-dataset :cars))
  ($ :speed cars)


  (with-data cars
    (def lm (linear-model ($ :dist) ($ :speed)))
    (doto (scatter-plot ($ :speed) ($ :dist))
      view
      (add-lines ($ :speed) (:fitted lm))))

  ;; standardize speed and dist and append the standardized variables to the original dataset
  (with-data (get-dataset :cars)
    (view (conj-cols $data
                     (sweep (sweep ($ :speed)) :stat sd :fun div)
                     (sweep (sweep ($ :dist)) :stat sd :fun div))))

  (with-data (get-dataset :iris)
    (view $data)
    (view ($ [:Sepal.Length :Sepal.Width :Species]))
    (view ($ [:not :Petal.Width :Petal.Length]))
    (view ($ 0 [:not :Petal.Width :Petal.Length])))

  (use 'incanter.core)
   (def mat (matrix (range 9) 3))
   (view mat)
   ($ 2 2 mat)
(view   ($ [0 2] 2 mat))
(view   ($ :all 1 mat))
  (view ($ 1 mat))
   (view ($ [:not 1] mat))
 (view  ($ 0 :all mat))
   (view ($ [0 2] [0 2] mat))
   (view ($ [:not 1] [:not 1] mat))
   (view ($ [:not 0] :all mat))
   ($ [0 2] [:not 1] mat)
   ($ [0 2] [:not 1 2] mat)
   ($ [0 2] [:not (range 2)] mat)
   ($ [:not (range 2)] [0 2] mat)