# view-pager-adapter [![](https://ci.novoda.com/buildStatus/icon?job=view-pager-adapter)](https://ci.novoda.com/job/view-pager-adapter/lastBuild/console) [![Download](https://api.bintray.com/packages/novoda/maven/view-pager-adapter/images/download.svg) ](https://bintray.com/novoda/maven/view-pager-adapter/_latestVersion) [![](https://raw.githubusercontent.com/novoda/novoda/master/assets/btn_apache_lisence.png)](LICENSE.txt)

A simple implementation of PagerAdapter that supports Views.

## Description

A ViewPagerAdapter for your ViewPager, this implementation will attempt to rebind to existing views when you call `notifyDataSetChanged()`, rather than recreate all the Views.

## Adding to your project

To start using this library, add these lines to the `build.gradle` of your project:

```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.novoda:view-pager-adapter:<latest-version>'
}
```

## Simple usage

`ViewPagerAdapter` is typed so you can specify the type of `View` explicitly. If you're making a ViewPager with just TextViews, you can use the following implementation:

```java
class TextViewPagerAdapter extends ViewPagerAdapter<TextView> {
    
    private List<String> text;

    TextViewPagerAdapter(List<String> text) {
        this.text = text;
    }

    public void update(List<String> text) {
        this.text = text;
        notifyDataSetChanged();
    }

    @Override
    protected TextView createView(ViewGroup container, int position) {
        // inflate the view, do not attach to parent (the `false` param at the end of the `inflate()`)
        TextView view = (TextView) LayoutInflater.from(container.getContext()).inflate(R.layout.view_my_text_view, container, false);
        return view;
    }

    @Override
    protected void bindView(TextView view, int position) {
        String textForView = text.get(position);
        view.setText(textForView);
    }

    @Override
    public int getCount() {
        return text.size();
    }

}
```

## Links

Here are a list of useful links:

 * We always welcome people to contribute new features or bug fixes, please see our [contributing guide](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md) before opening a pull request
 * If you have a problem check the [Issues Page](https://github.com/novoda/view-pager-adapter/issues) first to see if we are working on it
