<template>
  <div class="registro-articulos">
    <div class="menu-indicator" @mouseenter="mostrarMenu">
      Menú
    </div>

    <nav
        class="menu-bar"
        :class="{ oculto: !menuVisible }"
        @mouseenter="mostrarMenu"
        @mouseleave="ocultarMenu"
    >
      <ul>
        <li>
          <router-link to="/gestion" active-class="active">Productos</router-link>
        </li>
        <li>
          <router-link to="/gestion/pedidos" active-class="active">Pedidos</router-link>
        </li>
        <li>
          <router-link to="/gestion/ventas" active-class="active">Ventas</router-link>
        </li>
        <li>
          <router-link to="/gestion/lotes" active-class="active">Lotes</router-link>
        </li>
      </ul>
    </nav>

    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'RegistroArticulosLayout',
  data() {
    return {
      menuVisible: false,
    };
  },
  methods: {
    mostrarMenu() {
      this.menuVisible = true;
    },
    ocultarMenu() {
      this.menuVisible = false;
    },
  },
};
</script>

<style>
/* Global styles affecting html and body */
html, body {
  margin: 0;
  padding: 0;
  /* Background for html/body can be removed or set to transparent if .registro-articulos covers fully */
  /* background-color: #ffcc00; */
  height: 100%;
  width: 100%;
  overflow-x: hidden;
}
</style>

<style scoped>
.registro-articulos {
  /* REMOVED: background-color: #ffcc00; to allow image to show */
  min-height: 100vh;
  width: 100vw;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 100px;
  padding-bottom: 50px;
  background-image: url('@/assets/articulos.jpg');
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
  background-attachment: fixed;

}

.menu-indicator {
  position: fixed;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 200px;
  height: 45px;
  background-color: #ffd700;
  border-radius: 0 0 12px 12px;
  z-index: 1001;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.2rem;
  color: #222;
  transition: background-color 0.3s ease;
}

.menu-indicator:hover {
  background-color: #ffcc00;
}

.menu-bar {
  position: fixed;
  top: 45px;
  left: 50%;
  transform: translateX(-50%);
  background-color: #333;
  color: white;
  padding: 2rem 6rem;
  border-radius: 40px;
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.35);
  z-index: 1000;
  font-size: 1.2rem;
  transition: top 0.4s ease;
}

.menu-bar.oculto {
  top: -150px;
}

.menu-bar ul {
  list-style: none;
  display: flex;
  justify-content: center;
  gap: 3rem;
  margin: 0;
  padding: 0;
}

.menu-bar li {
  cursor: pointer;
  font-weight: bold;
}

.menu-bar a {
  color: white;
  text-decoration: none;
  font-size: 1rem;
  padding: 0.75rem 1.5rem;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.menu-bar a:hover {
  background-color: #444;
}

.menu-bar a.active {
  background-color: #ffd700;
  color: #000;
  font-weight: bold;
}
</style>