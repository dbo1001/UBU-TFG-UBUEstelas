package com.example.ubuestelas.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.GridView;

import com.example.ubuestelas.activities.TypePuzzleActivity;

/**
 * Vista de malla con un detector de gestos.
 *
 * @author Dave Park
 * @author Marcos Pena
 */
public class GestureDetectGridView extends GridView {
    private GestureDetector gDetector;
    private boolean mFlingConfirmed = false;
    private float mTouchX;
    private float mTouchY;

    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_MAX_OFF_PATH = 100;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    /**
     * Inicialización de la vista.
     * @param context Contexto de la aplicación.
     */
    public GestureDetectGridView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Inicialización de la vista.
     * @param context Contexto de la aplicación.
     * @param attrs Atributos asociados a la vista.
     */
    public GestureDetectGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Inicialización de la vista.
     * @param context Contexto de la aplicación.
     * @param attrs Atributos asociados a la vista.
     * @param defStyleAttr
     */
    public GestureDetectGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Inicialización de la vista.
     * @param context Contexto de la aplicación
     * @param attrs Atributos asociados a la vista.
     * @param defStyleAttr
     * @param defStyleRes
     */
    public GestureDetectGridView(Context context, AttributeSet attrs, int defStyleAttr,
                                 int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    /**
     * Inicializa la vista. Añade el Listener de gestos para cuando se mueve una pieza.
     * @param context Contexto de la aplicación.
     */
    private void init(final Context context) {
        gDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            /**
             * Detecta cuando se toca en la pantalla.
             * @param event El evento de tocar la pantalla
             * @return true si el evento es consumido, false si no.
             */
            @Override
            public boolean onDown(MotionEvent event) {
                return true;
            }

            /**
             * Detecta cuando se realiza un evento de arrastre.
             * @param e1 El evento de tocar la pantalla donde se comienza el arrastre.
             * @param e2 El evento de movimiento del arrastre.
             * @param velocityX Velocidad medida en píxeles del eje X.
             * @param velocityY Velocidad medida en píxeles del eje Y.
             * @return true si el evento es consumido, false si no.
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                final int position = GestureDetectGridView.this.pointToPosition
                        (Math.round(e1.getX()), Math.round(e1.getY()));

                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                    if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH
                            || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
                        return false;
                    }
                    if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) {
                        TypePuzzleActivity.moveTiles(context, TypePuzzleActivity.up, position);
                    } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE) {
                        TypePuzzleActivity.moveTiles(context, TypePuzzleActivity.down, position);
                    }
                } else {
                    if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
                        return false;
                    }
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
                        TypePuzzleActivity.moveTiles(context, TypePuzzleActivity.left, position);
                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
                        TypePuzzleActivity.moveTiles(context, TypePuzzleActivity.right, position);
                    }
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    /**
     * Intercepta todos los eventos de moviento de la pantalla táctil.
     * @param ev el evento de movimiento enviado.
     * @return true para realizar los eventos de movimiento en esta vista.
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        gDetector.onTouchEvent(ev);

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mFlingConfirmed = false;
        } else if (action == MotionEvent.ACTION_DOWN) {
            mTouchX = ev.getX();
            mTouchY = ev.getY();
        } else {

            if (mFlingConfirmed) {
                return true;
            }

            float dX = (Math.abs(ev.getX() - mTouchX));
            float dY = (Math.abs(ev.getY() - mTouchY));
            if ((dX > SWIPE_MIN_DISTANCE) || (dY > SWIPE_MIN_DISTANCE)) {
                mFlingConfirmed = true;
                return true;
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * Maneja los eventos de movimiento.
     * @param ev Evento de movimiento.
     * @return true si el evento se ha manejado, false si no.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return gDetector.onTouchEvent(ev);
    }
}
