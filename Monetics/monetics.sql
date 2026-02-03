-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 03-02-2026 a las 11:54:01
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `monetics`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `auditoria_gastos`
--

CREATE TABLE `auditoria_gastos` (
  `id_auditoria` bigint(20) NOT NULL,
  `id_gasto` bigint(20) NOT NULL,
  `estado_anterior` varchar(30) DEFAULT NULL,
  `estado_nuevo` varchar(30) NOT NULL,
  `id_usuario_accion` bigint(20) NOT NULL,
  `fecha_cambio` timestamp NOT NULL DEFAULT current_timestamp(),
  `comentario` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `auditoria_gastos`
--

INSERT INTO `auditoria_gastos` (`id_auditoria`, `id_gasto`, `estado_anterior`, `estado_nuevo`, `id_usuario_accion`, `fecha_cambio`, `comentario`) VALUES
(1, 1, 'BORRADOR', 'PENDIENTE_APROBACION', 3, '2026-01-29 17:35:01', 'Envío para aprobación'),
(2, 2, 'PENDIENTE_APROBACION', 'APROBADO', 2, '2026-01-29 17:35:01', 'Gasto correcto'),
(3, 3, 'PENDIENTE_APROBACION', 'RECHAZADO', 2, '2026-01-29 17:35:01', 'Falta justificación');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `departamentos`
--

CREATE TABLE `departamentos` (
  `id_departamento` bigint(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `presupuesto_mensual` decimal(12,2) NOT NULL,
  `presupuesto_anual` decimal(14,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `departamentos`
--

INSERT INTO `departamentos` (`id_departamento`, `nombre`, `presupuesto_mensual`, `presupuesto_anual`) VALUES
(1, 'Tecnología', 50000.00, 600000.00),
(2, 'Finanzas', 30000.00, 360000.00),
(3, 'Recursos Humanos', 15000.00, 180000.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `gastos`
--

CREATE TABLE `gastos` (
  `id_gasto` bigint(20) NOT NULL,
  `id_usuario` bigint(20) NOT NULL,
  `id_departamento` bigint(20) NOT NULL,
  `descripcion` varchar(255) NOT NULL,
  `importe_original` decimal(12,2) NOT NULL,
  `moneda_original` varchar(10) NOT NULL,
  `importe_eur` decimal(12,2) NOT NULL,
  `tipo_cambio` decimal(10,4) NOT NULL,
  `estado_gasto` varchar(30) NOT NULL,
  `fecha_gasto` date NOT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `gastos`
--

INSERT INTO `gastos` (`id_gasto`, `id_usuario`, `id_departamento`, `descripcion`, `importe_original`, `moneda_original`, `importe_eur`, `tipo_cambio`, `estado_gasto`, `fecha_gasto`, `fecha_creacion`) VALUES
(1, 3, 1, 'Comida viaje Londres', 120.00, 'GBP', 138.00, 1.1500, 'PENDIENTE_APROBACION', '2026-01-10', '2026-01-29 17:35:01'),
(2, 4, 1, 'Hotel conferencia Berlín', 300.00, 'EUR', 300.00, 1.0000, 'APROBADO', '2026-01-05', '2026-01-29 17:35:01'),
(3, 5, 2, 'Taxi reunión clientes', 45.00, 'EUR', 45.00, 1.0000, 'RECHAZADO', '2026-01-08', '2026-01-29 17:35:01'),
(4, 3, 1, 'Billete avión', 0.00, 'EUR', 0.00, 1.0000, 'BORRADOR', '2026-01-12', '2026-01-29 17:35:01');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `presupuestos`
--

CREATE TABLE `presupuestos` (
  `id_presupuesto` bigint(20) NOT NULL,
  `id_departamento` bigint(20) NOT NULL,
  `tipo_periodo` varchar(20) NOT NULL,
  `anio` int(11) NOT NULL,
  `mes` int(11) DEFAULT NULL,
  `importe_limite` decimal(14,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `presupuestos`
--

INSERT INTO `presupuestos` (`id_presupuesto`, `id_departamento`, `tipo_periodo`, `anio`, `mes`, `importe_limite`) VALUES
(1, 1, 'ANUAL', 2026, NULL, 600000.00),
(2, 1, 'MENSUAL', 2026, 1, 50000.00),
(3, 2, 'ANUAL', 2026, NULL, 360000.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id_usuario` bigint(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `rol` varchar(30) NOT NULL,
  `id_departamento` bigint(20) NOT NULL,
  `id_manager` bigint(20) DEFAULT NULL,
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `email`, `password`, `rol`, `id_departamento`, `id_manager`, `activo`) VALUES
(1, 'Administrador Sistema', 'admin@monetics.com', 'admin123', 'ROLE_ADMIN', 1, NULL, 1),
(2, 'Laura Manager Tech', 'laura.manager@monetics.com', 'manager123', 'ROLE_MANAGER', 1, NULL, 1),
(3, 'Carlos Developer', 'carlos.dev@monetics.com', 'user123', 'ROLE_USER', 1, 2, 1),
(4, 'Ana Developer', 'ana.dev@monetics.com', 'user123', 'ROLE_USER', 1, 2, 1),
(5, 'Mario Finanzas', 'mario.finanzas@monetics.com', 'user123', 'ROLE_USER', 2, NULL, 1);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `auditoria_gastos`
--
ALTER TABLE `auditoria_gastos`
  ADD PRIMARY KEY (`id_auditoria`),
  ADD KEY `id_gasto` (`id_gasto`),
  ADD KEY `id_usuario_accion` (`id_usuario_accion`);

--
-- Indices de la tabla `departamentos`
--
ALTER TABLE `departamentos`
  ADD PRIMARY KEY (`id_departamento`);

--
-- Indices de la tabla `gastos`
--
ALTER TABLE `gastos`
  ADD PRIMARY KEY (`id_gasto`),
  ADD KEY `id_usuario` (`id_usuario`),
  ADD KEY `id_departamento` (`id_departamento`);

--
-- Indices de la tabla `presupuestos`
--
ALTER TABLE `presupuestos`
  ADD PRIMARY KEY (`id_presupuesto`),
  ADD KEY `id_departamento` (`id_departamento`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `id_departamento` (`id_departamento`),
  ADD KEY `id_manager` (`id_manager`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `auditoria_gastos`
--
ALTER TABLE `auditoria_gastos`
  MODIFY `id_auditoria` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `departamentos`
--
ALTER TABLE `departamentos`
  MODIFY `id_departamento` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `gastos`
--
ALTER TABLE `gastos`
  MODIFY `id_gasto` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `presupuestos`
--
ALTER TABLE `presupuestos`
  MODIFY `id_presupuesto` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id_usuario` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `auditoria_gastos`
--
ALTER TABLE `auditoria_gastos`
  ADD CONSTRAINT `auditoria_gastos_ibfk_1` FOREIGN KEY (`id_gasto`) REFERENCES `gastos` (`id_gasto`),
  ADD CONSTRAINT `auditoria_gastos_ibfk_2` FOREIGN KEY (`id_usuario_accion`) REFERENCES `usuarios` (`id_usuario`);

--
-- Filtros para la tabla `gastos`
--
ALTER TABLE `gastos`
  ADD CONSTRAINT `gastos_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `gastos_ibfk_2` FOREIGN KEY (`id_departamento`) REFERENCES `departamentos` (`id_departamento`);

--
-- Filtros para la tabla `presupuestos`
--
ALTER TABLE `presupuestos`
  ADD CONSTRAINT `presupuestos_ibfk_1` FOREIGN KEY (`id_departamento`) REFERENCES `departamentos` (`id_departamento`);

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`id_departamento`) REFERENCES `departamentos` (`id_departamento`),
  ADD CONSTRAINT `usuarios_ibfk_2` FOREIGN KEY (`id_manager`) REFERENCES `usuarios` (`id_usuario`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
