package com.chaoyige.store;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {
    private final List<Product> products = new ArrayList<>();
    private final Map<Integer, Integer> cart = new HashMap<>();
    private final Set<Integer> favorites = new HashSet<>();
    private FrameLayout content;
    private LinearLayout bottomNav;
    private int activeTab = 0;

    private final int pink = Color.parseColor("#FF3D8D");
    private final int purple = Color.parseColor("#7857FF");
    private final int cyan = Color.parseColor("#00BFD1");
    private final int ink = Color.parseColor("#202033");
    private final int muted = Color.parseColor("#7D7D91");
    private final int page = Color.parseColor("#FFF8FC");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#6C4CFF"));
        seedProducts();
        buildShell();
        showHome("");
    }

    private void seedProducts() {
        products.add(new Product(1, "雾灰廓形卫衣", "卫衣", 159, 199, "轻柔毛圈面料，宽松落肩版型，低调但有层次。", "灰色 / 黑色 / 奶白", "M L XL 2XL", "#697180", "4.9", "1.2k"));
        products.add(new Product(2, "奶油色针织开衫", "上衣", 139, 179, "柔软细针织，简洁小翻领，适合校园日常叠穿。", "奶油白 / 浅咖", "M L XL", "#D7BFA8", "4.8", "856"));
        products.add(new Product(3, "混色直筒休闲裤", "裤装", 169, 219, "细腻混色纹理，直筒不贴腿，通勤和校园都自然。", "灰棕 / 深灰", "28 29 30 31 32 33", "#85796F", "4.9", "2.3k"));
        products.add(new Product(4, "海盐蓝宽松衬衫", "衬衫", 129, 169, "清爽海盐蓝，微落肩剪裁，可单穿也可作薄外套。", "海盐蓝 / 白", "M L XL", "#71A7D8", "4.7", "634"));
        products.add(new Product(5, "黑曜轻机能夹克", "外套", 259, 329, "轻量防风面料，多口袋但不夸张，适合城市出行。", "黑色 / 石墨灰", "M L XL 2XL", "#343842", "4.9", "973"));
        products.add(new Product(6, "燕麦色垂感长裤", "裤装", 179, 229, "高垂感面料，腰头简洁，适合喜欢干净穿搭的人。", "燕麦 / 深咖", "28 29 30 31 32", "#C9B498", "4.8", "1.1k"));
        products.add(new Product(7, "葡萄紫短袖T恤", "T恤", 89, 119, "220g纯棉，饱和度克制的葡萄紫，单穿有记忆点。", "葡萄紫 / 黑", "S M L XL", "#7254A8", "4.8", "3.5k"));
        products.add(new Product(8, "青雾运动板鞋", "鞋履", 229, 299, "低帮简约轮廓，青灰拼色，适合休闲裤和卫裤。", "青灰 / 米白", "39 40 41 42 43 44", "#73A8A2", "4.7", "721"));
    }

    private void buildShell() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(page);

        content = new FrameLayout(this);
        root.addView(content, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        bottomNav = new LinearLayout(this);
        bottomNav.setOrientation(LinearLayout.HORIZONTAL);
        bottomNav.setPadding(dp(6), dp(5), dp(6), dp(6));
        bottomNav.setBackgroundColor(Color.WHITE);
        root.addView(bottomNav, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(70)));
        setContentView(root);
        renderNav();
    }

    private void renderNav() {
        bottomNav.removeAllViews();
        String[] titles = {"首页", "分类", "收藏", "购物车", "我的"};
        String[] icons = {"⌂", "▦", "♡", "🛒", "☺"};
        for (int i = 0; i < titles.length; i++) {
            final int tab = i;
            TextView tv = new TextView(this);
            String badge = (i == 3 && cartCount() > 0) ? "  ·" + cartCount() : "";
            tv.setText(icons[i] + "\n" + titles[i] + badge);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(12);
            tv.setTypeface(null, i == activeTab ? Typeface.BOLD : Typeface.NORMAL);
            tv.setTextColor(i == activeTab ? pink : muted);
            tv.setOnClickListener(v -> switchTab(tab));
            bottomNav.addView(tv, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        }
    }

    private void switchTab(int tab) {
        activeTab = tab;
        renderNav();
        if (tab == 0) showHome("");
        if (tab == 1) showCategories();
        if (tab == 2) showFavorites();
        if (tab == 3) showCart();
        if (tab == 4) showProfile();
    }

    private void showHome(String query) {
        activeTab = 0;
        renderNav();
        LinearLayout box = vertical();
        box.setPadding(dp(16), dp(12), dp(16), dp(18));

        LinearLayout brand = horizontal();
        TextView logo = label("潮衣阁", 28, Color.WHITE, true);
        TextView slogan = label("  穿出你的低调潮流", 12, Color.parseColor("#EEE9FF"), false);
        brand.addView(logo);
        brand.addView(slogan, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        brand.setPadding(dp(16), dp(16), dp(16), dp(16));
        brand.setBackground(gradient("#FF3D8D", "#6A52FF", 20));
        box.addView(brand, matchWrap());
        spacer(box, 12);

        LinearLayout search = horizontal();
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setText(query);
        input.setHint("搜卫衣、裤装、衬衫…");
        input.setTextSize(14);
        input.setPadding(dp(14), 0, dp(10), 0);
        input.setBackground(round(Color.WHITE, 18));
        search.addView(input, new LinearLayout.LayoutParams(0, dp(48), 1));
        Button go = button("搜索", pink, Color.WHITE);
        go.setOnClickListener(v -> showHome(input.getText().toString().trim()));
        search.addView(go, new LinearLayout.LayoutParams(dp(78), dp(48)));
        box.addView(search, matchWrap());
        spacer(box, 14);

        LinearLayout promo = vertical();
        promo.setPadding(dp(18), dp(18), dp(18), dp(18));
        promo.setBackground(gradient("#00C8D7", "#735DFF", 22));
        promo.addView(label("NEW SEASON  ·  秋日上新", 13, Color.WHITE, true));
        promo.addView(label("满 299 减 40", 28, Color.WHITE, true));
        promo.addView(label("学生认证再享 95 折 · 演示优惠", 12, Color.parseColor("#E9FFFF"), false));
        box.addView(promo, matchWrap());
        spacer(box, 16);

        box.addView(sectionTitle("精选分类"));
        HorizontalScrollView hsv = new HorizontalScrollView(this);
        hsv.setHorizontalScrollBarEnabled(false);
        LinearLayout chips = horizontal();
        for (String c : new String[]{"全部", "卫衣", "裤装", "衬衫", "外套", "T恤", "鞋履"}) {
            TextView chip = label(c, 13, ink, true);
            chip.setPadding(dp(16), dp(9), dp(16), dp(9));
            chip.setBackground(round(Color.WHITE, 18));
            chip.setOnClickListener(v -> showFiltered(c));
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cp.setMargins(0, 0, dp(8), 0);
            chips.addView(chip, cp);
        }
        hsv.addView(chips);
        box.addView(hsv, matchWrap());
        spacer(box, 16);

        box.addView(sectionTitle(query.isEmpty() ? "本周热卖" : "搜索结果"));
        List<Product> list = new ArrayList<>();
        for (Product p : products) {
            String key = query.toLowerCase(Locale.ROOT);
            if (key.isEmpty() || p.name.toLowerCase(Locale.ROOT).contains(key) || p.category.toLowerCase(Locale.ROOT).contains(key)) list.add(p);
        }
        if (list.isEmpty()) box.addView(empty("没有找到相关商品，换个关键词试试"));
        else addProductGrid(box, list);
        setPage(box);
    }

    private void showFiltered(String category) {
        if ("全部".equals(category)) { showHome(""); return; }
        LinearLayout box = vertical();
        box.setPadding(dp(16), dp(12), dp(16), dp(18));
        box.addView(topBar(category + "专区", () -> showHome("")));
        List<Product> list = new ArrayList<>();
        for (Product p : products) if (p.category.equals(category)) list.add(p);
        if (list.isEmpty()) box.addView(empty("该分类正在上新")); else addProductGrid(box, list);
        setPage(box);
    }

    private void showCategories() {
        LinearLayout box = vertical();
        box.setPadding(dp(16), dp(16), dp(16), dp(18));
        box.addView(label("商品分类", 26, ink, true));
        box.addView(label("按你的穿搭需求快速逛", 13, muted, false));
        spacer(box, 16);
        String[][] cats = {{"卫衣", "宽松 / 连帽 / 圆领", "#A36BFF"}, {"裤装", "直筒 / 垂感 / 混色", "#4E8E88"}, {"衬衫", "校园 / 通勤 / 叠穿", "#67A3D5"}, {"外套", "机能 / 夹克 / 秋冬", "#414754"}, {"T恤", "纯棉 / 重磅 / 简约", "#A15B78"}, {"鞋履", "板鞋 / 日常 / 百搭", "#6B9D8B"}};
        for (String[] c : cats) {
            LinearLayout card = horizontal();
            TextView icon = label("●", 38, Color.parseColor(c[2]), true);
            card.addView(icon, new LinearLayout.LayoutParams(dp(54), dp(70)));
            LinearLayout txt = vertical();
            txt.addView(label(c[0], 18, ink, true));
            txt.addView(label(c[1], 12, muted, false));
            card.addView(txt, new LinearLayout.LayoutParams(0, dp(70), 1));
            card.addView(label("›", 30, muted, false));
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dp(14), dp(8), dp(14), dp(8));
            card.setBackground(round(Color.WHITE, 18));
            card.setOnClickListener(v -> showFiltered(c[0]));
            LinearLayout.LayoutParams mp = matchWrap(); mp.setMargins(0, 0, 0, dp(10));
            box.addView(card, mp);
        }
        setPage(box);
    }

    private void showFavorites() {
        LinearLayout box = vertical();
        box.setPadding(dp(16), dp(16), dp(16), dp(18));
        box.addView(label("我的收藏", 26, ink, true));
        spacer(box, 12);
        List<Product> list = new ArrayList<>();
        for (Product p : products) if (favorites.contains(p.id)) list.add(p);
        if (list.isEmpty()) box.addView(empty("还没有收藏商品\n逛到喜欢的就点一下 ♡")); else addProductGrid(box, list);
        setPage(box);
    }

    private void showCart() {
        LinearLayout box = vertical();
        box.setPadding(dp(16), dp(16), dp(16), dp(18));
        box.addView(label("购物车  " + cartCount() + " 件", 26, ink, true));
        spacer(box, 12);
        if (cart.isEmpty()) {
            box.addView(empty("购物车还是空的\n去首页挑几件喜欢的吧"));
            setPage(box); return;
        }
        double total = 0;
        for (Product p : products) {
            Integer qty = cart.get(p.id);
            if (qty == null) continue;
            total += p.price * qty;
            LinearLayout row = horizontal();
            TextView block = label("衣", 22, Color.WHITE, true);
            block.setGravity(Gravity.CENTER);
            block.setBackground(round(Color.parseColor(p.color), 16));
            row.addView(block, new LinearLayout.LayoutParams(dp(68), dp(78)));
            LinearLayout info = vertical();
            info.setPadding(dp(12), 0, 0, 0);
            info.addView(label(p.name, 15, ink, true));
            info.addView(label("¥" + p.price + "   × " + qty, 14, pink, true));
            LinearLayout actions = horizontal();
            Button minus = button("−", Color.parseColor("#F1EEF7"), ink);
            Button plus = button("+", Color.parseColor("#F1EEF7"), ink);
            TextView num = label(String.valueOf(qty), 14, ink, true); num.setGravity(Gravity.CENTER);
            minus.setOnClickListener(v -> changeQty(p.id, -1));
            plus.setOnClickListener(v -> changeQty(p.id, 1));
            actions.addView(minus, new LinearLayout.LayoutParams(dp(38), dp(36)));
            actions.addView(num, new LinearLayout.LayoutParams(dp(44), dp(36)));
            actions.addView(plus, new LinearLayout.LayoutParams(dp(38), dp(36)));
            info.addView(actions);
            row.addView(info, new LinearLayout.LayoutParams(0, dp(88), 1));
            row.setPadding(dp(10), dp(10), dp(10), dp(10));
            row.setBackground(round(Color.WHITE, 18));
            LinearLayout.LayoutParams rp = matchWrap(); rp.setMargins(0, 0, 0, dp(10));
            box.addView(row, rp);
        }
        spacer(box, 8);
        LinearLayout settle = vertical();
        settle.setPadding(dp(16), dp(14), dp(16), dp(14));
        settle.setBackground(round(Color.WHITE, 20));
        settle.addView(label("商品合计", 13, muted, false));
        settle.addView(label("¥" + (int) total, 30, pink, true));
        Button checkout = button("去结算", pink, Color.WHITE);
        final int money = (int) total;
        checkout.setOnClickListener(v -> showCheckout(money));
        settle.addView(checkout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(50)));
        box.addView(settle, matchWrap());
        setPage(box);
    }

    private void showCheckout(int money) {
        LinearLayout form = vertical();
        form.setPadding(dp(18), dp(4), dp(18), dp(4));
        EditText name = field("收货人姓名");
        EditText phone = field("手机号");
        EditText addr = field("收货地址");
        form.addView(name); form.addView(phone); form.addView(addr);
        new AlertDialog.Builder(this)
                .setTitle("确认订单 · ¥" + money)
                .setView(form)
                .setMessage("当前为演示版本，不会发起真实支付。")
                .setNegativeButton("取消", null)
                .setPositiveButton("模拟下单", (d, w) -> {
                    cart.clear(); renderNav();
                    Toast.makeText(this, "下单成功（演示订单）", Toast.LENGTH_LONG).show();
                    showProfile();
                }).show();
    }

    private void showProfile() {
        LinearLayout box = vertical();
        box.setPadding(dp(16), dp(16), dp(16), dp(18));
        LinearLayout head = horizontal();
        TextView avatar = label("潮", 24, Color.WHITE, true); avatar.setGravity(Gravity.CENTER); avatar.setBackground(round(purple, 50));
        head.addView(avatar, new LinearLayout.LayoutParams(dp(66), dp(66)));
        LinearLayout info = vertical(); info.setPadding(dp(12), dp(5), 0, 0);
        info.addView(label("潮衣阁会员", 20, ink, true)); info.addView(label("普通会员 · 完善资料可解锁更多权益", 12, muted, false));
        head.addView(info, new LinearLayout.LayoutParams(0, dp(66), 1));
        box.addView(head);
        spacer(box, 16);

        LinearLayout stats = horizontal(); stats.setBackground(round(Color.WHITE, 18)); stats.setPadding(dp(8), dp(14), dp(8), dp(14));
        stats.addView(stat("0", "待付款"), new LinearLayout.LayoutParams(0, dp(54), 1));
        stats.addView(stat("0", "待收货"), new LinearLayout.LayoutParams(0, dp(54), 1));
        stats.addView(stat(String.valueOf(favorites.size()), "收藏"), new LinearLayout.LayoutParams(0, dp(54), 1));
        stats.addView(stat("3", "优惠券"), new LinearLayout.LayoutParams(0, dp(54), 1));
        box.addView(stats);
        spacer(box, 14);

        String[] menu = {"我的订单", "收货地址", "优惠券中心", "尺码助手", "客服与售后", "关于潮衣阁"};
        for (String m : menu) {
            TextView item = label(m + "                                      ›", 15, ink, false);
            item.setPadding(dp(16), dp(17), dp(10), dp(17));
            item.setBackground(round(Color.WHITE, 14));
            item.setOnClickListener(v -> Toast.makeText(this, ((TextView)v).getText().toString().replace("›", "").trim() + "（演示功能）", Toast.LENGTH_SHORT).show());
            LinearLayout.LayoutParams ip = matchWrap(); ip.setMargins(0, 0, 0, dp(8)); box.addView(item, ip);
        }
        TextView note = label("潮衣阁 v1.0.0\n当前 APK 为本地演示商城：商品、收藏、购物车可使用；支付、登录、物流和真实订单需接入服务器后才能上线运营。", 12, muted, false);
        note.setPadding(dp(10), dp(16), dp(10), dp(10)); box.addView(note);
        setPage(box);
    }

    private void showDetail(Product p) {
        LinearLayout box = vertical();
        box.setPadding(dp(16), dp(12), dp(16), dp(20));
        box.addView(topBar("商品详情", () -> switchTab(0)));
        TextView hero = label("潮\n衣", 52, Color.WHITE, true); hero.setGravity(Gravity.CENTER); hero.setBackground(gradient(p.color, "#8F78FF", 26));
        box.addView(hero, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(280)));
        spacer(box, 14);
        box.addView(label(p.name, 24, ink, true));
        box.addView(label("¥" + p.price + "   原价 ¥" + p.oldPrice, 20, pink, true));
        box.addView(label("★ " + p.rating + "   ·   " + p.sales + " 人已购", 13, muted, false));
        spacer(box, 14);
        box.addView(label("商品介绍", 16, ink, true));
        box.addView(label(p.desc, 14, Color.parseColor("#555565"), false));
        spacer(box, 12);
        box.addView(label("颜色：" + p.colors, 14, ink, false));
        box.addView(label("尺码：" + p.sizes, 14, ink, false));
        spacer(box, 16);
        LinearLayout acts = horizontal();
        Button fav = button(favorites.contains(p.id) ? "已收藏" : "♡ 收藏", Color.parseColor("#F2ECFF"), purple);
        Button add = button("加入购物车", cyan, Color.WHITE);
        Button buy = button("立即购买", pink, Color.WHITE);
        fav.setOnClickListener(v -> { toggleFavorite(p.id); showDetail(p); });
        add.setOnClickListener(v -> { addCart(p.id); Toast.makeText(this, "已加入购物车", Toast.LENGTH_SHORT).show(); });
        buy.setOnClickListener(v -> { addCart(p.id); showCart(); });
        acts.addView(fav, new LinearLayout.LayoutParams(0, dp(50), 1));
        acts.addView(add, new LinearLayout.LayoutParams(0, dp(50), 1));
        acts.addView(buy, new LinearLayout.LayoutParams(0, dp(50), 1));
        box.addView(acts);
        setPage(box);
    }

    private void addProductGrid(LinearLayout parent, List<Product> list) {
        for (int i = 0; i < list.size(); i += 2) {
            LinearLayout row = horizontal();
            row.setGravity(Gravity.TOP);
            row.addView(productCard(list.get(i)), new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            spacerHorizontal(row, 10);
            if (i + 1 < list.size()) row.addView(productCard(list.get(i + 1)), new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            else row.addView(new View(this), new LinearLayout.LayoutParams(0, 1, 1));
            LinearLayout.LayoutParams rp = matchWrap(); rp.setMargins(0, 0, 0, dp(10)); parent.addView(row, rp);
        }
    }

    private View productCard(Product p) {
        LinearLayout card = vertical();
        card.setBackground(round(Color.WHITE, 18));
        TextView image = label("衣", 34, Color.WHITE, true); image.setGravity(Gravity.CENTER); image.setBackground(gradient(p.color, "#8B72FF", 18));
        card.addView(image, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(150)));
        LinearLayout info = vertical(); info.setPadding(dp(10), dp(10), dp(10), dp(10));
        info.addView(label(p.name, 15, ink, true));
        info.addView(label(p.category + "  ·  ★ " + p.rating, 11, muted, false));
        LinearLayout price = horizontal();
        price.addView(label("¥" + p.price, 18, pink, true), new LinearLayout.LayoutParams(0, dp(42), 1));
        TextView fav = label(favorites.contains(p.id) ? "♥" : "♡", 25, favorites.contains(p.id) ? pink : muted, true); fav.setGravity(Gravity.CENTER);
        fav.setOnClickListener(v -> { toggleFavorite(p.id); if (activeTab == 2) showFavorites(); else showHome(""); });
        price.addView(fav, new LinearLayout.LayoutParams(dp(38), dp(42)));
        info.addView(price);
        Button add = button("加入购物车", Color.parseColor("#F4F0FF"), purple); add.setOnClickListener(v -> { addCart(p.id); Toast.makeText(this, "已加入购物车", Toast.LENGTH_SHORT).show(); });
        info.addView(add, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(40)));
        card.addView(info);
        card.setOnClickListener(v -> showDetail(p));
        return card;
    }

    private LinearLayout topBar(String title, Runnable back) {
        LinearLayout bar = horizontal(); bar.setGravity(Gravity.CENTER_VERTICAL); bar.setPadding(0, 0, 0, dp(12));
        TextView b = label("‹", 34, ink, false); b.setGravity(Gravity.CENTER); b.setOnClickListener(v -> back.run());
        bar.addView(b, new LinearLayout.LayoutParams(dp(44), dp(44)));
        bar.addView(label(title, 22, ink, true), new LinearLayout.LayoutParams(0, dp(44), 1));
        return bar;
    }

    private void addCart(int id) { cart.put(id, cart.getOrDefault(id, 0) + 1); renderNav(); }
    private void changeQty(int id, int d) { int q = cart.getOrDefault(id, 0) + d; if (q <= 0) cart.remove(id); else cart.put(id, q); showCart(); renderNav(); }
    private void toggleFavorite(int id) { if (favorites.contains(id)) favorites.remove(id); else favorites.add(id); }
    private int cartCount() { int n = 0; for (int q : cart.values()) n += q; return n; }

    private void setPage(LinearLayout box) {
        ScrollView sc = new ScrollView(this); sc.setFillViewport(true); sc.addView(box);
        content.removeAllViews(); content.addView(sc, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private LinearLayout vertical() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private LinearLayout horizontal() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL); return l; }
    private TextView label(String text, int sp, int color, boolean bold) { TextView t = new TextView(this); t.setText(text); t.setTextSize(sp); t.setTextColor(color); if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD); t.setGravity(Gravity.CENTER_VERTICAL); return t; }
    private TextView sectionTitle(String s) { TextView t = label(s, 19, ink, true); t.setPadding(0, 0, 0, dp(10)); return t; }
    private TextView empty(String s) { TextView t = label(s, 15, muted, false); t.setGravity(Gravity.CENTER); t.setPadding(dp(20), dp(70), dp(20), dp(70)); return t; }
    private EditText field(String hint) { EditText e = new EditText(this); e.setHint(hint); e.setSingleLine(true); e.setPadding(dp(12), 0, dp(12), 0); e.setBackground(round(Color.parseColor("#F4F2F8"), 12)); LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(48)); p.setMargins(0, dp(6), 0, dp(6)); e.setLayoutParams(p); return e; }
    private Button button(String text, int bg, int fg) { Button b = new Button(this); b.setText(text); b.setTextColor(fg); b.setTextSize(13); b.setAllCaps(false); b.setBackground(round(bg, 14)); return b; }
    private View stat(String a, String b) { LinearLayout l = vertical(); TextView x = label(a, 18, pink, true); x.setGravity(Gravity.CENTER); TextView y = label(b, 11, muted, false); y.setGravity(Gravity.CENTER); l.addView(x, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30)); l.addView(y, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 24)); return l; }
    private GradientDrawable round(int color, int radius) { GradientDrawable g = new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(radius)); return g; }
    private GradientDrawable gradient(String a, String b, int radius) { GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{Color.parseColor(a), Color.parseColor(b)}); g.setCornerRadius(dp(radius)); return g; }
    private LinearLayout.LayoutParams matchWrap() { return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); }
    private void spacer(LinearLayout l, int h) { l.addView(new View(this), new LinearLayout.LayoutParams(1, dp(h))); }
    private void spacerHorizontal(LinearLayout l, int w) { l.addView(new View(this), new LinearLayout.LayoutParams(dp(w), 1)); }
    private int dp(int n) { return (int) (n * getResources().getDisplayMetrics().density + 0.5f); }

    static class Product {
        int id, price, oldPrice; String name, category, desc, colors, sizes, color, rating, sales;
        Product(int id, String name, String category, int price, int oldPrice, String desc, String colors, String sizes, String color, String rating, String sales) {
            this.id=id; this.name=name; this.category=category; this.price=price; this.oldPrice=oldPrice; this.desc=desc; this.colors=colors; this.sizes=sizes; this.color=color; this.rating=rating; this.sales=sales;
        }
    }
}
