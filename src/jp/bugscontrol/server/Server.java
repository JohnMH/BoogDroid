package jp.bugscontrol.server;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockListActivity;

import jp.bugscontrol.AdapterBug;
import jp.bugscontrol.AdapterProduct;

public abstract class Server {
    protected List<Product> products;
    protected String name, url, user, password;

    AdapterProduct adapter_product;
    AdapterBug adapter_bug;
    SherlockListActivity products_activity, bugs_activity;

    public Server(String name, String url) {
        this.name = name;
        this.url = url;
        user = "";
        password = "";
        products = new ArrayList<Product>();
    }

    public Server(jp.bugscontrol.db.Server db_server) {
        name = db_server.name;
        url = db_server.url;
        user = db_server.user;
        password = db_server.password;
        products = new ArrayList<Product>();
    }

    protected abstract void loadProducts();
    protected abstract void loadBugsForProduct(Product p);

    public void setUser(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public void setAdapterProduct(AdapterProduct adapter, SherlockListActivity activity) {
        adapter_product = adapter;
        products_activity = activity;

        products_activity.setSupportProgressBarIndeterminateVisibility(true);
        loadProducts();
    }

    public void setAdapterBug(Product product, AdapterBug adapter, SherlockListActivity activity) {
        adapter_bug = adapter;
        bugs_activity = activity;

        bugs_activity.setSupportProgressBarIndeterminateVisibility(true);
        loadBugsForProduct(product);
    }

    public List<Product> getProducts() {return products;}

    protected void productsListUpdated() {
        adapter_product.notifyDataSetChanged();
        products_activity.setSupportProgressBarIndeterminateVisibility(false);
    }

    protected void bugsListUpdated() {
        adapter_bug.notifyDataSetChanged();
        bugs_activity.setSupportProgressBarIndeterminateVisibility(false);
    }

    public Product getProductFromId(int product_id) {
        for (Product p : products)
            if (p.getId() == product_id)
                return p;
        return null;
    }

    public void save() {
        jp.bugscontrol.db.Server db_server = new jp.bugscontrol.db.Server();
        db_server.name = name;
        db_server.url = url;
        db_server.user = user;
        db_server.password = password;
        db_server.save();
    }

    public String getName()     {return name;}
    public String getUrl()      {return url;}
    public String getUser()     {return user;}
    public String getPassword() {return password;}

    public boolean hasUser() {return user.length() > 0;}
}