package kg.manas.ssn.adapters;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import kg.manas.ssn.R;
import kg.manas.ssn.view.profile.LessonsMarksFragment;
import kg.manas.ssn.view.profile.PersonalInformationFragment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileSectionsPagerAdapter extends FragmentPagerAdapter {
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private Fragment[] fragments = new Fragment[2];
    private final Context mContext;

    public ProfileSectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        fragments[0] = PersonalInformationFragment.newInstance(0);
        fragments[1] = LessonsMarksFragment.newInstance(1);
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
