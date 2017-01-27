/*
 *  BugsControl
 *  Copyright (C) 2013  Jon Ander Peñalba
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.albfan.boogdroid.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.albfan.boogdroid.R;
import me.albfan.boogdroid.general.Product;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.ViewHolder> implements Filterable {
    private List<Product> values;
    private List<Product> fullValues;
    private final ProductListFragment fragment;
    private Filter filter;

    public AdapterProduct(final List<Product> values, final ProductListFragment fragment) {
        this.fullValues = values;
        this.values = values;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int i) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_product, parent, false);
        return new ViewHolder(v, fragment);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int i) {
        final Product p = values.get(i);
        holder.name.setText(p.getName());
        holder.description.setText(p.getDescription());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public int getProductIdFromPosition(final int position) {
        return values.get(position).getId();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults filterResults = new FilterResults();
                    if (charSequence == null || charSequence.length() == 0) {
                        filterResults.count = fullValues.size();
                        filterResults.values = fullValues;
                    } else {
                        List<Product> nProducts = new ArrayList<>();
                        for (Product product : fullValues) {
                            if (product.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                nProducts.add(product);
                            }
                        }
                        filterResults.count = nProducts.size();
                        filterResults.values = nProducts;
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    values = (List<Product>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
        return filter;
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView description;

        public ViewHolder(final View v, final ProductListFragment fragment) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            description = (TextView) v.findViewById(R.id.description);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragment.onListItemClick(getPosition());
                }
            });
        }
    }
}
