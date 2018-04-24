package fi.timomcfarlane.tellmewhen.utils;

import android.view.View;

/**
 * Interface used in setting CardViews inside list onItemClick listeners
 *
 * @author  Timo McFarlane
 * @version 1.0
 * @since   2014-04-24
 */
public interface CustomCardClickListener {
    /**
     * When clicking view, pass current view position inside list and view
     * @param v View that was clicked
     * @param position Position inside list
     */
    public void onItemClick(View v, int position);
}
