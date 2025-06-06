
package com.beerstar.beerstar.service;

import com.beerstar.beerstar.response.responsePedido;
import com.beerstar.beerstar.response.responseProveedores;
import com.beerstar.beerstar.response.responseProductos;
import com.beerstar.beerstar.response.responseUsuario;
import com.beerstar.beerstar.response.responseLotes; 
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ApiService {

    @GET("api/beers") 
    Call<List<responseProductos>> obtenerProductos();

    @GET("api/batches")
    Call<List<responseLotes>> getLotes();

    @POST("api/auth/register") 
    Call<responseUsuario> registrarUsuario(@Body Usuario usuario);

    @POST("api/orders")
    Call<responsePedido> crearPedido(@Body responsePedido pedido);

    @POST("api/auth/login")
    Call<responseUsuario> loginUsuario(@Body Usuario usuario);

    @PUT("beerstar/usuarios/clientes/{id}") 
    Call<responseUsuario> actualizarUsuario(@Path("id") long id, @Body Usuario usuario);

    @GET("api/public/providers") 
    Call<List<responseProveedores>> getProveedores();
    // Opcional: Para obtener los pedidos de un usuario (si lo necesitas)
    @GET("orders/user/{userId}")
    Call<List<responsePedido>> getOrdersByUserId(@Path("userId") Long userId);

    // Opcional: Para obtener los pedidos de un proveedor (si lo necesitas)
    @GET("orders/provider/{providerId}")
    Call<List<responsePedido>> getOrdersByProviderId(@Path("providerId") Long providerId);
}
    
