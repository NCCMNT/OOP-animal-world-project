# OOP Animal World Project 🐾

Interactive simulation of an animal ecosystem with UI and statistical tracking, built in Java.

## Table of Contents

- [About](#about)  
- [Features](#features)  
- [Requirements](#requirements)

## About

The **OOP Animal World Project** is a simulation of a virtual world populated with animals and plants. The project demonstrates object-oriented programming techniques and design principles, while allowing the user to observe emergent behavior, as well as collect metrics and statistics about the simulated ecosystem.

This repository contains the Java source code, UI frontend, configuration modules, and statistical/graphical output.

## Features

- Grid-based world where animals move, eat, reproduce, age, and die  
- Edge wrapping in east-west direction (“toroidal” in x-axis)  
- Restricted movement north/south beyond poles — animals reflect/change direction  
- Random mutations of genes (full random change)  
- Sequential gene execution (predestination)  
- Special “large plants” in certain regions, which grant higher energy on consumption  
- Aging animals move slower; probability of skipping movement increases with age (up to 80%)  
- UI visualization of the simulated world  
- Statistics, charts, logs for tracking population, gene distributions, energy, etc.

## Requirements

- Java Development Kit (JDK) 11 or later  
- A GUI environment (e.g. graphical desktop) for displaying UI  
