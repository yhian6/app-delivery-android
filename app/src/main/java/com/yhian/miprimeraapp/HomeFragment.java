package com.yhian.miprimeraapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yhian.miprimeraapp.adapter.CategoryAdapter;
import com.yhian.miprimeraapp.adapter.ProductAdapter;
import com.yhian.miprimeraapp.adapter.SliderAdapter;
import com.yhian.miprimeraapp.modelo.Category;
import com.yhian.miprimeraapp.modelo.Product;
import com.yhian.miprimeraapp.modelo.SliderItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerSlider, recyclerRecomendados;
    private SliderAdapter sliderAdapter;
    private List<SliderItem> sliderList;
    private Handler handler = new Handler();
    private Runnable runnable;
    private int currentPosition = 0;
    private LinearLayout sliderIndicatorLayout;
    private List<Product> allProductos = new ArrayList<>();
    private ProductAdapter productoAdapter;


    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        insertarCategoriasFirebase();
        insertarProductosFirebase();
        insertarSlidersFirebase();

        ImageButton btnMenu = view.findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), btnMenu);
            popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_config) {
                    // 👉 Ir a configuración
                    Intent intent = new Intent(getContext(), PerfilActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });

            popup.show();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txtUserName = view.findViewById(R.id.txtUserName);
        TextView txtUserEmail = view.findViewById(R.id.txtUserEmail);
        ImageView imgUserProfile = view.findViewById(R.id.imgUserProfile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Aquí guardas el nombre en el perfil de FirebaseAuth
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(String.valueOf(txtUserName)) // este es el nombre que el usuario puso en tu formulario
                .build();


        if (user != null) {
            String email = user.getEmail();
            txtUserEmail.setText(email);

            String name = user.getDisplayName();
            String photoUrl = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;

            if (name != null && !name.isEmpty()) {
                txtUserName.setText(name);
            } else {
                String uid = user.getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nombre = snapshot.child("nombre").getValue(String.class);
                        txtUserName.setText(nombre != null ? nombre : "Usuario");
                       // cargarDatosUsuarioFragment();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        txtUserName.setText("Usuario");
                    }
                });
            }

            if (photoUrl != null) {
                Glide.with(this).load(photoUrl).circleCrop().into(imgUserProfile);
            } else {
                imgUserProfile.setImageResource(R.drawable.user);
            }
        }

        // Categorías
        RecyclerView recyclerCategories = view.findViewById(R.id.recyclerCategories);
        recyclerCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<Category> categoryList = new ArrayList<>();
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        recyclerCategories.setAdapter(categoryAdapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categorias");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Category category = snap.getValue(Category.class);
                    categoryList.add(category);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar categorías", Toast.LENGTH_SHORT).show();
            }
        });

        // Slider
        recyclerSlider = view.findViewById(R.id.recyclerSlider);
        recyclerSlider.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        sliderList = new ArrayList<>();
        sliderAdapter = new SliderAdapter(getContext(), sliderList);
        recyclerSlider.setAdapter(sliderAdapter);

        recyclerSlider.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int position = layoutManager.findFirstVisibleItemPosition();
                if (position != currentPosition) {
                    currentPosition = position;
                    updateSliderIndicators(position);
                }
            }
        });

        // Recomendados
        recyclerRecomendados = view.findViewById(R.id.recyclerRecomendados);
        recyclerRecomendados.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        List<Product> recomendadosList = new ArrayList<>();
        ProductAdapter recomendadosAdapter = new ProductAdapter(getContext(), recomendadosList);
        recyclerRecomendados.setAdapter(recomendadosAdapter);

        DatabaseReference productosRef = FirebaseDatabase.getInstance().getReference("Productos");
        productosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recomendadosList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Product p = snap.getValue(Product.class);
                    if (p != null && "ACTIVO".equals(p.getEstado()) && p.isRecomendado()) {
                        recomendadosList.add(p);
                    }
                }
                recomendadosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar recomendados", Toast.LENGTH_SHORT).show();
            }
        });


        DatabaseReference sliderRef = FirebaseDatabase.getInstance().getReference("Slider");
        sliderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sliderList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    SliderItem item = snap.getValue(SliderItem.class);
                    sliderList.add(item);
                }
                sliderAdapter.notifyDataSetChanged();

                // Nueva parte: Indicadores
                createSliderIndicators(sliderList.size());

                handler.removeCallbacks(runnable);
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (sliderList.size() == 0) return;
                        if (currentPosition == sliderList.size()) {
                            currentPosition = 0;
                        }
                        recyclerSlider.smoothScrollToPosition(currentPosition++);
                        handler.postDelayed(this, 4000);
                    }
                };
                handler.postDelayed(runnable, 4000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar sliders", Toast.LENGTH_SHORT).show();
            }
        });

// Buscar
        EditText edtSearch = view.findViewById(R.id.edtSearch);

// Cuando haga clic en el buscador
        edtSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchResultsActivity.class);
            startActivity(intent);
        });


        imgUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PerfilActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void cargarDatosUsuarioFragment() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String uid = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);

            ImageView imgUserProfile = getView().findViewById(R.id.imgUserProfile);
            TextView txtUserName = getView().findViewById(R.id.txtUserName);

            // Este listener se mantiene activo y se ejecuta cada vez que hay un cambio
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String apellidos = snapshot.child("apellidos").getValue(String.class);
                    String fotoNombre = snapshot.child("foto").getValue(String.class);

                    txtUserName.setText(nombre + (apellidos != null ? " " + apellidos : ""));

                    if(fotoNombre != null && !fotoNombre.isEmpty()){
                        File file = new File(getContext().getFilesDir(), fotoNombre);
                        if(file.exists()){
                            Glide.with(HomeFragment.this)
                                    .load(file)
                                    .circleCrop()
                                    .into(imgUserProfile);
                        } else {
                            imgUserProfile.setImageResource(R.drawable.user);
                        }
                    } else {
                        imgUserProfile.setImageResource(R.drawable.user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        if (runnable != null) {
            handler.postDelayed(runnable, 4000);
        }
    }

    private void insertarCategoriasFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categorias");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    List<Category> categorias = new ArrayList<>();
                    categorias.add(new Category("Menú del día", "https://www.patioelrubio.es/wp-content/uploads/2023/09/Menu-del-dia-en-Linares-Nuevo-Patio-el-Rubio-menu-diario-martes-a-viernes.jpg"));
                    categorias.add(new Category("Refrescos y gaseosas", "https://entre7calderos.com/wp-content/uploads/2017/12/Refrescos_800.jpg"));
                    categorias.add(new Category("Cerveza", "https://larepublica.cronosmedia.glr.pe/original/2023/06/14/6489d4c20f9a7a05cf06feb6.jpg"));


                    for (Category categoria : categorias) {
                        String id = categoria.getName();
                        categoria.setId(id);
                        ref.child(id).setValue(categoria);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al insertar categorías", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertarProductosFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Productos");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> productos = new ArrayList<>();
                productos.add(new Product(null, "Aji de gallina",
                        "Un clásico de la gastronomía peruana. Este delicioso plato combina pollo desmenuzado en una cremosa salsa a base de ají amarillo, pan remojado, leche y especias. Se sirve acompañado de arroz blanco, papa sancochada, huevo duro y aceitunas, ofreciendo un sabor suave, ligeramente picante y lleno de tradición.",
                        15.00,
                        "https://sabordelobueno.com/wp-content/uploads/2020/11/receta-aji-de-gallina.jpg",
                        "Menú del día", "ACTIVO", true));

                productos.add(new Product(null, "Lomo saltado",
                        "Un sabroso emblema de la cocina peruana que fusiona lo mejor de la tradición criolla y la influencia oriental. Preparado con jugosos trozos de carne salteados al wok con cebolla, tomate y ají, sazonados con sillao y especias. Se sirve acompañado de papas fritas y arroz blanco, creando una combinación irresistible de sabores y texturas.",
                        15.00,
                        "https://i.blogs.es/b0a5c0/lomo_saltado/450_1000.jpg",
                        "Menú del día", "ACTIVO", false));

                productos.add(new Product(null, "Arroz con mariscos",
                        "Una explosión de sabor marino en cada bocado. Este delicioso plato combina arroz graneado con una mezcla de mariscos frescos, salteados con ají, cebolla, ajo y especias peruanas. Se sirve caliente, con un toque de culantro y limón, ofreciendo un aroma irresistible y un sabor único",
                        20.00,
                        "https://peru.info/archivos/publicacion/210-imagen-106565112021.jpg",
                        "Menú del día", "ACTIVO", false));

                productos.add(new Product(null, "Arroz con cachema frita",
                        "Un plato tradicional y lleno de sabor casero. Acompaña una jugosa cachema frita, de carne suave y dorada, con arroz blanco y una porción de menestra bien sazonada. Una combinación perfecta que representa lo mejor de la cocina criolla peruana.",
                        8.00,
                        "https://live.staticflickr.com/2818/11278955615_6010d43c28_z.jpg",
                        "Menú del día", "ACTIVO", true));

                productos.add(new Product(null, "Copus",
                        "Delicia tradicional del norte peruano. Preparado con carnes sazonadas (cerdo, res y pollo), arroz, yuca y plátano, todo cocido bajo tierra en hojas de plátano al calor del fuego. Su sabor ahumado y textura suave lo convierten en un plato típico lleno de historia y tradición piurana.",
                        20.00,
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/Copus.jpg/1200px-Copus.jpg",
                        "Menú del día", "ACTIVO", true));

                productos.add(new Product(null, "Coca Cola 500 ml",
                        "La clásica bebida gaseosa con su inconfundible sabor refrescante. Ideal para acompañar tus comidas y disfrutar cada momento.",
                        3.50,
                        "https://yopo.pe/wp-content/uploads/2023/12/COCA-500-ORIGINAL-RAPPI.jpg",
                        "Refrescos y gaseosas", "ACTIVO", true));

                productos.add(new Product(null, "Inca Kola 500 ml",
                        "La bebida del sabor nacional. Dulce, burbujeante y perfecta para resaltar el sabor de tus platos peruanos favoritos.",
                        3.50,
                        "https://plazavea.vteximg.com.br/arquivos/ids/28516749-418-418/497497.jpg",
                        "Refrescos y gaseosas", "ACTIVO", true));

                productos.add(new Product(null, "Inca Kola 1L",
                        "El inconfundible sabor del Perú en presentación familiar. Refresca y comparte con todos, ideal para el almuerzo o una comida entre amigos.",
                        8.50,
                        "https://delosi-pidelo.s3.amazonaws.com/chilis/products/inca-kola-sin-azucar-1-litro-202501071828125358.jpg",
                        "Refrescos y gaseosas", "ACTIVO", false));
                productos.add(new Product(null, "Fanta 500 ml",
                        "Refrescante y divertida, con su sabor intenso a naranja y burbujas llenas de energía. Perfecta para acompañar cualquier plato.",
                        2.50,
                        "https://www.normita.com/wp-content/uploads/2020/07/fanta-500ml.jpg",
                        "Refrescos y gaseosas", "ACTIVO", false));

                productos.add(new Product(null, "Chicha morada",
                        "Refrescante bebida tradicional peruana elaborada con maíz morado, piña, canela y clavo de olor. Dulce, natural y perfecta para acompañar cualquier plato típico.",
                        3.50,
                        "https://sergeyca.com/assets/uploads/c4f2de7b125db1459e5206d605492a3e.jpeg",
                        "Refrescos y gaseosas", "ACTIVO", true));

                productos.add(new Product(null, "Chicha de jora",
                        "Auténtica bebida artesanal preparada con maíz fermentado al estilo tradicional de Cura Mori. Su sabor suave y ligeramente ácido refleja la esencia de la costa norte y sus costumbres.",
                        4.50,
                        "https://comidasperuanas.com.pe/wp-content/uploads/2024/03/chicha_jora.jpg",
                        "Refrescos y gaseosas", "ACTIVO", true));

                productos.add(new Product(null, "Cerveza Cristal 500 ml",
                        "La cerveza dorada del Perú. Refrescante, ligera y con el sabor clásico que acompaña perfectamente cualquier comida.",
                        8.00,
                        "https://plazachevere.com/4224-home_default/cristal-650ml-caja-12-botellas.jpg",
                        "Cerveza", "ACTIVO", true));

                productos.add(new Product(null, "Cerveza Pilsen 500 ml",
                        "Tradición y sabor auténtico. Una cerveza de cuerpo balanceado y espuma suave, ideal para compartir entre amigos.",
                        8.00,
                        "https://nazcadelivery.com/wp-content/uploads/2020/11/Cerveza-Pilsen-Caja-12-botellas.png",
                        "Cerveza", "ACTIVO", true));

                productos.add(new Product(null, "Cerveza Cusqueña Negra 500 ml",
                        "Una cerveza premium de color oscuro y sabor intenso, con notas a malta tostada y caramelo. Ideal para acompañar carnes y platos criollos.",
                        10.00,
                        "https://corporacionliderperu.com/41314-large_default/cerveza-cusquena-malta-negra-bt-x-620-ml.jpg",
                        "Cerveza", "ACTIVO", false));

                productos.add(new Product(null, "Cerveza Cusqueña Trigo 500 ml",
                        "Refrescante y suave, elaborada con trigo malteado y un toque cítrico. Perfecta para quienes disfrutan de una cerveza ligera y con aroma natural.",
                        10.00,
                        "https://delosi-pidelo.s3.amazonaws.com/chilis/products/cerveza-cusquena-de-trigo-310-ml-202501071828116057.jpg",
                        "Cerveza", "ACTIVO", false));





                for (Product producto : productos) {
                    boolean existe = false;
                    String idExistente = null;

                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Product existente = snap.getValue(Product.class);
                        if (existente != null && existente.getNombre().equals(producto.getNombre())) {
                            existe = true;
                            idExistente = existente.getId();
                            break;
                        }
                    }

                    if (!existe) {
                        // Insertar nuevo producto
                        String id = ref.push().getKey();
                        producto.setId(id);
                        ref.child(id).setValue(producto);
                    } else {
                        // ⚡ Actualizar campos faltantes de los productos viejos
                        ref.child(idExistente).child("estado").setValue(producto.getEstado());
                        ref.child(idExistente).child("recomendado").setValue(producto.isRecomendado());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al insertar productos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void insertarSlidersFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Slider");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    List<String> urls = new ArrayList<>();
                    urls.add("https://tse3.mm.bing.net/th/id/OIP.PtS-g1Bse7O7c8EoDJsLagHaE1?pid=Api&P=0&h=180");
                    urls.add("https://tse4.mm.bing.net/th/id/OIP.5NPSN1InHv8VWoW0gpCBiwHaFG?pid=Api&P=0&h=180");
                    urls.add("https://tse4.mm.bing.net/th/id/OIP.6ydZnkh0LPChcSYbiah_dQHaE3?pid=Api&P=0&h=180");
                    urls.add("https://tse2.mm.bing.net/th/id/OIP.N-7eZXrM9MiGuY_7QR5uEwHaE6?pid=Api&P=0&h=180");

                    for (String url : urls) {
                        String key = ref.push().getKey();
                        if (key != null) {
                            ref.child(key).setValue(new SliderItem(url));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al insertar sliders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createSliderIndicators(int count) {
        sliderIndicatorLayout = getView().findViewById(R.id.sliderIndicatorLayout);
        sliderIndicatorLayout.removeAllViews();

        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(getContext());
            dot.setImageResource(R.drawable.indicator_inactive);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);

            sliderIndicatorLayout.addView(dot);
        }

        if (count > 0) {
            updateSliderIndicators(0);
        }
    }

    private void updateSliderIndicators(int position) {
        if (sliderIndicatorLayout == null) return;

        int childCount = sliderIndicatorLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView dot = (ImageView) sliderIndicatorLayout.getChildAt(i);
            if (i == position) {
                dot.setImageResource(R.drawable.indicator_active);
            } else {
                dot.setImageResource(R.drawable.indicator_inactive);
            }
        }
    }


}
