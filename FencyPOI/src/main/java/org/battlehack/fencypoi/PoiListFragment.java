package org.battlehack.fencypoi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by aschildbach on 6/8/13.
 */
public class PoiListFragment extends ListFragment {

    private Poi poi;
    private POIAdapter adapter;
    private Cursor poiCursor;
    private ActionMode mActionMode;
    private int mActionModeItemPosition;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        poiCursor = getActivity().managedQuery(POIDBContentProvider.CONTENT_URI, null, null, null, null);
        adapter = new POIAdapter(getActivity(), poiCursor, true);

        setListAdapter(adapter);
    }

    private class POIAdapter extends CursorAdapter {

        public POIAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).inflate(R.layout.poi_list_entry, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView nameTextView = (TextView) view.findViewById(R.id.name);
            TextView typeTextView = (TextView) view.findViewById(R.id.type);

            nameTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_NAME)));
            typeTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(POIDBContentProvider.KEY_TYPE)));

            if (mActionMode != null && cursor.getPosition() == mActionModeItemPosition) {
                view.setBackgroundResource(R.drawable.background_btn);
            } else {
                view.setBackgroundDrawable(null);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onResume() {

        super.onResume();

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mActionModeItemPosition = i;

                poi = Poi2CursorConverter.fromCursor(poiCursor, mActionModeItemPosition);

                App.getBus().post(poi);

                if (mActionMode == null) {
                    mActionMode = getActivity().startActionMode(mActionModeCallback);
                }


                adapter.notifyDataSetChanged();
            }
        });


        getListView().setEmptyView(getView().findViewById(android.R.id.empty));
        getActivity().invalidateOptionsMenu();
    }


    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.list_action_mode, menu);

            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_share:
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + poi.getLatDbl() + "," + poi.getLonDbl() + "(" + poi.getName() + ")"));
                    startActivity(Intent.createChooser(intent, "Share"));
                    mode.finish();
                    return true;
                case R.id.menu_delete:
                    /*String where = BaseColumns._ID + "='" + poi.getID() + "'";
                    getActivity().getContentResolver().delete(POIDBContentProvider.CONTENT_URI, where, null);
                    */
                    getActivity().getContentResolver().delete(poi.getUri(), null, null);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }


        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.invalidateOptionsMenu();
    }
}
