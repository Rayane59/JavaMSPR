# mspr-epsi-2021
### Projet d'intégration continue MSPR-EPSI 2021

# Démo à la promo du 03-01-2022

# Projet Java

# La classe com.app.console.Main fonctionne selon 2 modes
	- Mode interactif où les questions sont posées à l'écran
	- Mode commande où les paramètres sont passés en ligne de commande

# Exemples : 
## batch mode  
	--name DUPONT --firstname Eloïse
	--name DERDIER --firstname Charle-Henry

## interactive mode
	--I


<h2 style="text-align: center;">Build & Delivery</h2>  

### ant file with 3 targets

 - compile
 - package
 - info
 
### Ajout de tests d'intégration 
- création d'un job spécifique dans Jenkins  

### Paramètrage GitLib<->Jenkins  
	- serveur Tomcat hébergeant GitBlit : http://localhost:8101 (Url direct -> http://localhost:8101/jenkins
	- serveur Jenkins : http://localhost:9090/jenkins (devops1, devops1)
	- Attention si changement de directory pour GitBlit alors faire les modifications dans le nouveau répertoire
	- Activer les post-receive hook dans GitBlit  

