package jp.ac.chiba_fjb.x14b_c.naroreader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oikawa on 2016/12/10.
 */



public class CustomMenu implements Menu {
    class CustomItem implements MenuItem {
        int mId;
        String mTitle;
        Drawable mIconDraable;
        int mIconId;



        public CustomItem(int itemId, CharSequence title) {
            mId = itemId;
            mTitle = title.toString();
        }

        @Override
        public int getItemId() {
            return mId;
        }

        @Override
        public int getGroupId() {
            return 0;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public MenuItem setTitle(CharSequence charSequence) {
            mTitle=charSequence.toString();
            return this;
        }

        @Override
        public MenuItem setTitle(int i) {

            mTitle = getContext().getString(i);
            return null;
        }

        @Override
        public CharSequence getTitle() {
            return mTitle;
        }

        @Override
        public MenuItem setTitleCondensed(CharSequence charSequence) {
            return this;
        }

        @Override
        public CharSequence getTitleCondensed() {
            return null;
        }

        @Override
        public MenuItem setIcon(Drawable drawable) {
            return this;
        }

        @Override
        public MenuItem setIcon(int i) {
            mIconId=i;return this;
        }

        @Override
        public Drawable getIcon() {

            if(mIconDraable != null)
                return mIconDraable;
            if(mIconId != 0)
                return ContextCompat.getDrawable(getContext(),mIconId);
            return null;
        }

        @Override
        public MenuItem setIntent(Intent intent) {
            return this;
        }

        @Override
        public Intent getIntent() {
            return null;
        }

        @Override
        public MenuItem setShortcut(char c, char c1) {
            return this;
        }

        @Override
        public MenuItem setNumericShortcut(char c) {
            return this;
        }

        @Override
        public char getNumericShortcut() {
            return 0;
        }

        @Override
        public MenuItem setAlphabeticShortcut(char c) {
            return this;
        }

        @Override
        public char getAlphabeticShortcut() {
            return 0;
        }

        @Override
        public MenuItem setCheckable(boolean b) {
            return this;
        }

        @Override
        public boolean isCheckable() {
            return false;
        }

        @Override
        public MenuItem setChecked(boolean b) {
            return this;
        }

        @Override
        public boolean isChecked() {
            return false;
        }

        @Override
        public MenuItem setVisible(boolean b) {
            return this;
        }

        @Override
        public boolean isVisible() {
            return false;
        }

        @Override
        public MenuItem setEnabled(boolean b) {
            return this;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public boolean hasSubMenu() {
            return false;
        }

        @Override
        public SubMenu getSubMenu() {
            return null;
        }

        @Override
        public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
            return this;
        }

        @Override
        public ContextMenu.ContextMenuInfo getMenuInfo() {
            return null;
        }

        @Override
        public void setShowAsAction(int i) {

        }

        @Override
        public MenuItem setShowAsActionFlags(int i) {
            return this;
        }

        @Override
        public MenuItem setActionView(View view) {
            return this;
        }

        @Override
        public MenuItem setActionView(int i) {
            return this;
        }

        @Override
        public View getActionView() {
            return null;
        }

        @Override
        public MenuItem setActionProvider(ActionProvider actionProvider) {
            return this;
        }

        @Override
        public ActionProvider getActionProvider() {
            return null;
        }

        @Override
        public boolean expandActionView() {
            return false;
        }

        @Override
        public boolean collapseActionView() {
            return false;
        }

        @Override
        public boolean isActionViewExpanded() {
            return false;
        }

        @Override
        public MenuItem setOnActionExpandListener(OnActionExpandListener onActionExpandListener) {
            return this;
        }
    }
    private List<MenuItem> mItems = new ArrayList<MenuItem>();

    private Context mContext;
    public CustomMenu(Context context){
        mContext = context;
    }
    public Context getContext(){
        return mContext;
    }

    @Override
    public MenuItem add(CharSequence charSequence) {
        return null;
    }

    @Override
    public MenuItem add(int i) {
        return null;
    }

    @Override
    public MenuItem add(int groupId,
                        int itemId,
                        int order,
                        CharSequence title) {
        MenuItem item = new CustomItem(itemId,title);
        mItems.add(item);

        return item;
    }

    @Override
    public MenuItem add(int i, int i1, int i2, int i3) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(CharSequence charSequence) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int i) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int groupId,
                              int itemId,
                              int order,
                              CharSequence title) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int i, int i1, int i2, int i3) {
        return null;
    }

    @Override
    public int addIntentOptions(int i, int i1, int i2, ComponentName componentName, Intent[] intents, Intent intent, int i3, MenuItem[] menuItems) {
        return 0;
    }

    @Override
    public void removeItem(int i) {

    }

    @Override
    public void removeGroup(int i) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void setGroupCheckable(int i, boolean b, boolean b1) {

    }

    @Override
    public void setGroupVisible(int i, boolean b) {

    }

    @Override
    public void setGroupEnabled(int i, boolean b) {

    }

    @Override
    public boolean hasVisibleItems() {
        return false;
    }

    @Override
    public MenuItem findItem(int i) {
        return null;
    }

    @Override
    public int size() {
        return mItems.size();
    }

    @Override
    public MenuItem getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean performShortcut(int i, KeyEvent keyEvent, int i1) {
        return false;
    }

    @Override
    public boolean isShortcutKey(int i, KeyEvent keyEvent) {
        return false;
    }

    @Override
    public boolean performIdentifierAction(int i, int i1) {
        return false;
    }

    @Override
    public void setQwertyMode(boolean b) {

    }
}
