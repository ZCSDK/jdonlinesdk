package com.sobot.online.util;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sobot.online.fragment.SobotOnlineHistoryFragment;
import com.sobot.online.fragment.SobotOnlineReceptionFragment;

/**
 * @author 朱江宾
 * @Description: 碎片管理类
 * @2015-5-22
 */
public class FragTools {

    /**
     * @param manager FragmentManager
     * @param tag     Fragment的标识
     * @return
     * @Description: 通过 tag 获得指定的BackHandledFragment实例
     */
    public static Fragment getInstance(FragmentManager manager, String tag) {
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment == null) {
            if ("online".equals(tag)) {
                fragment = new SobotOnlineReceptionFragment();
            } else if ("history".equals(tag))
                fragment = new SobotOnlineHistoryFragment();
        }
        return fragment;
    }

    /**
     * 不带动画的添加Fragment
     *
     * @param manager
     * @param target
     * @param resourceId
     * @param tag
     */
    public static void addFragmet(FragmentManager manager, Fragment target,
                                  int resourceId, String tag) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(resourceId, target, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * 带动画的添加Fragment
     *
     * @param manager
     * @param target
     * @param resourceId
     */
    public static void addFragmetWithAnim(FragmentManager manager,
                                          Fragment target, int resourceId) {
        FragmentTransaction transaction = manager.beginTransaction();
        // transaction.setCustomAnimations(R.anim.fragment_trans_enter,
        // R.anim.fragment_trans_exit);
        transaction.add(resourceId, target);
        transaction.commit();
    }

    /**
     * 带动画的添加Fragment
     *
     * @param currate
     * @param target
     * @param resourceId
     */
    public static void addFragmetWithAnim(Fragment currate, Fragment target,
                                          int resourceId) {
        FragmentTransaction transaction = currate.getFragmentManager()
                .beginTransaction();
        // transaction.setCustomAnimations(R.anim.fragment_trans_enter,
        // R.anim.fragment_trans_exit);
        transaction.add(resourceId, target);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * 带动画的添加Fragment
     *
     * @param manager
     * @param target
     * @param resourceId
     */
    public static void addFragmetWithAnim(FragmentManager manager,
                                          Fragment target, int resourceId, String tag) {
        FragmentTransaction transaction = manager.beginTransaction();
        // transaction.setCustomAnimations(R.anim.fragment_trans_enter,
        // R.anim.fragment_trans_exit);
        transaction.add(resourceId, target, tag);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    /**
     * 不带动画的切换Fragment
     *
     * @param currate
     * @param target
     * @param resourceId
     */
    public static void replaceFragmet(Fragment currate, Fragment target,
                                      int resourceId) {
        FragmentTransaction transaction = currate.getFragmentManager()
                .beginTransaction();
        transaction.replace(resourceId, target);
        transaction.commit();
    }

    /**
     * 不带动画的切换Fragment
     *
     * @param activity
     * @param target
     * @param resourceId
     */
    public static void replaceFragmet(FragmentActivity activity,
                                      Fragment target, int resourceId) {
        FragmentTransaction transaction = activity.getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(resourceId, target);
        transaction.commit();
    }

    /**
     * 带动画的切换Fragment
     *
     * @param activity
     * @param target
     * @param resourceId
     */
    public static void replaceFragmetWithAnim(FragmentActivity activity,
                                              Fragment target, int resourceId) {
        FragmentTransaction transaction = activity.getSupportFragmentManager()
                .beginTransaction();
        // transaction.setCustomAnimations(R.anim.fragment_trans_enter,
        // R.anim.fragment_trans_exit);
        transaction.replace(resourceId, target);
        // transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * 带动画的切换Fragment
     *
     * @param currate
     * @param target
     * @param resourceId
     */
    public static void replaceFragmetWithAnim1(Fragment currate,
                                               Fragment target, int resourceId) {
        FragmentTransaction transaction = currate.getFragmentManager()
                .beginTransaction();
        // transaction.setCustomAnimations(R.anim.fragment_trans_enter,
        // R.anim.fragment_trans_exit);
        transaction.replace(resourceId, target);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * 带动画的移除一个Fragment
     *
     * @param manager
     * @param target
     */

    public static void removeFragmet(FragmentManager manager, Fragment target) {
        FragmentTransaction transaction = manager.beginTransaction();
        // transaction.setCustomAnimations(R.anim.fragment_trans_enter,
        // R.anim.fragment_trans_exit);
        transaction.remove(target);
        transaction.commit();

    }

    /**
     * @param manager
     * @param target
     * @Description:带动画显示
     */
    public static void showFragment(FragmentManager manager, Fragment target) {
        FragmentTransaction transaction = manager.beginTransaction();
        // transaction.setCustomAnimations(R.anim.fragment_trans_enter,
        // R.anim.fragment_trans_exit);
        transaction.show(target);
        transaction.commitAllowingStateLoss();
    }

    /**
     * @param manager
     * @param target
     * @Description:带动画隐藏
     */
    public static void hideFragment(FragmentManager manager, Fragment target) {
        FragmentTransaction transaction = manager.beginTransaction();
        // transaction.setCustomAnimations(R.anim.fragment_trans_enter,
        // R.anim.fragment_trans_exit);
        transaction.hide(target);
        transaction.commitAllowingStateLoss();
    }
}