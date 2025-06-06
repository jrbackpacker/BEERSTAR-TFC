<template>
  <div class="login-container">
    <div class="login-box">
      <h2>Login</h2>
      <form @submit.prevent="login">
        <input v-model="email" type="email" placeholder="Email" required />
        <input v-model="password" type="password" placeholder="Contraseña" required />
        <button type="submit">Entrar</button>
        <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      </form>
    </div>
  </div>
</template>

<script>
import { loginUser } from '@/services/api';

export default {
  name: 'LoginProveedor',
  data() {
    return {
      email: '',
      password: '',
      errorMessage: '',
    };
  },
  methods: {
    async login() {
      this.errorMessage = ''; // Limpiar mensajes de error anteriores
      try {
        const response = await loginUser(this.email, this.password,);
        console.log('Token recibido:', response.token); // <-- Log para depuración

        // Guardar el token y la información del usuario en localStorage
        localStorage.setItem('token', response.token);
        localStorage.setItem('userId', response.idUsuario);
        localStorage.setItem('userEmail', response.email);
        localStorage.setItem('userType', response.tipoUsuario);
        localStorage.setItem('userRoles', JSON.stringify(response.listaRoles));
        localStorage.setItem('userName', response.nombre || '');
        localStorage.setItem('userPhone', response.telefono || '');
        localStorage.setItem('userAddress', response.direccion || '');

        // Redirigir según roles
        if (response.listaRoles && response.listaRoles.includes('ROLE_PROVIDER')) {
          this.$router.push('/gestion-proveedor');
        } else if (response.listaRoles && response.listaRoles.includes('ROLE_CLIENT')) {
          this.$router.push('/dashboard-cliente');
        } else if (response.listaRoles && response.listaRoles.includes('ROLE_ADMIN')) {
          this.$router.push('/admin-panel');
        } else {
          this.$router.push('/gestion');
        }

      } catch (error) {
        console.error('Error en el login del componente:', error);
        if (error.response) {
          if (error.response.status === 401) {
            this.errorMessage = 'Email o contraseña incorrectos.';
          } else if (error.response.data && error.response.data.mensaje) {
            this.errorMessage = `Error: ${error.response.data.mensaje}`;
          } else {
            this.errorMessage = `Error al iniciar sesión: ${error.response.statusText}`;
          }
        } else if (error.request) {
          this.errorMessage = 'No se pudo conectar con el servidor. Inténtalo de nuevo más tarde.';
        } else {
          this.errorMessage = 'Ocurrió un error inesperado al iniciar sesión.';
        }
      }
    },

  },
};
</script>

<style scoped>
.login-container {
  background-image: url('@/assets/wall.jpg');
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
  background-attachment: fixed;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
.login-box {
  background: transparent;
  padding: 2rem;
  border-radius: 12px;
  text-align: center;
  width: 100%;
  max-width: 400px;
}
input, button {
  width: 100%;
  margin: 0.5rem 0;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 8px;
}
button {
  background-color: #333;
  color: white;
  cursor: pointer;
  font-weight: bold;
}
button:hover {
  background-color: #555;
}
.error-message {
  color: red;
  margin-top: 10px;
}
</style>