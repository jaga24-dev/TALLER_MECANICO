UABCS-Taller_Mecanico
Taller Mecánico UABCS Idea del diseño

USUARIO=carlos CONTRASEÑA=1234

El diseño se hizo pensando en que sea fácil de usar y rápido, ya que en un menu de taller la prioridad es la facilidad de utilizar una interfaz, en este caso para los trabajadores del taller.

Se buscó que todo fuera claro, simple y que cualquier persona pueda entenderlo sin confundirse.

Colores

Se eligieron los colores para que el sistema se vea profesional pero también cómodo de usar.

Azules #014461, #003045, transmiten orden y confianza Azul claro #056DBB para botones y acciones importantes Dorado #A59141 para resaltar información importante Rojo #FF0000 errores Naranja #FEA62C Resaltar titulos, y algunos botones. Grises y blancos para botones y fondos, y algunos indicadores visuales. Componentes

Se usaron elementos básicos pero bien organizados:

Botones claros y que sobresalen como guardar, editar, eliminar Formularios simples Tablas para visualizar datos Tarjetas para mostrar información resumida Navegación

Las Funciones de acceso son las siguiente: Iniciar sesión con sus respectivas alertas Cerrar sesión

El sistema tiene un menú sencillo para acceder a:

Dashboard Consultar clientes Crear orden Órdenes de servicio

La idea es que los administradores no tengan que dar muchos clics para encontrar lo que necesita.

Experiencia de usuario

Se buscó que:

No se vea saturado Sea fácil de aprender Ayude a evitar errores Conclusión

El diseño no es complejo, pero cumple con lo más importante, que funcione bien y sea fácil de usar, nuestra prioridad es que no sea un diseño el cual agote la vista del usuario.

El sistema funciona de la siguiente manera:

el administrador inicia sesión con las claves asignadas por el administrador de la base de datos. Este administrador tiene el poder de realizar las siguientes acciones dentro del sistema: visualizar datos de ventas semanales, eficiencia del taller, trabajos en curso, vehículos ingresados el día actual y sus estados añadir clientes a la base de datos, así como descargarlos o editarlos; lo mismo aplica para las órdenes.

Este sistema es únicamente administrativo, por lo cual no manejamos procesadores de pago. El display de dinero es únicamente estadístico, ingresado por los usuarios administrativos por lo tanto, el área de finanzas y los administradores del programa deben rendir cuentas para los pagos.


// Requisitos del sistema

Antes de ejecutar la aplicación es necesario contar con:

* Java 17 o una versión superior instalada.
* Sistema operativo Windows, Linux o macOS.
* Al menos 4 GB de memoria RAM.
* Aproximadamente 200 MB de espacio libre en disco.

// Instalación y ejecución

1. El archivo ejecutable .jar esta en la base del proyecta llamado TallerMecanico_b2.jar
2.Descargar o copiar el archivo UABCS-Taller_Mecanico.jar.
3. Abrir una terminal en la carpeta donde se encuentra el archivo.
4. Ejecutar el siguiente comando:

bash
java -jar UABCS-Taller_Mecanico.jar


4. Iniciar sesión utilizando las credenciales proporcionadas anteriormente.

---

// Estructura general del proyecto

La organización del proyecto sigue el modelo de vista controlador (MVC) - docs.

---

// Componentes de la interfaz

El sistema utiliza componentes simples pero funcionales, entre ellos:

* Botones para guardar, descargar archivos pdf, editar y eliminar registros.
* Formularios para captura de información.
* Tablas para visualizar datos de clientes y órdenes.
* Tarjetas informativas para mostrar estadísticas e indicadores.

// link del video
https://www.youtube.com/watch?v=Y8qn-1jk8vQ

// Detalles capturas pantalla TALLER_MECANICO - carpeta docs.
los detalles de las capturas de pantalla que describen como funciona el 
sistema esta dentro de la carpeta docs y se llama
Detalles pantallas Taller Mecanico.pdf

