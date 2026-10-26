## API Rest do projeto

#### Grupo 3 - 2CCOA

## Build e deploy

O workflow `.github/workflows/aws-deploy-dev.yml` compila o JAR com Java 21,
empacota-o no container e publica a imagem no ECR ao receber push em
`develop`. Depois, o Systems Manager atualiza o container na EC2
`InstanciaApp-01`.

A imagem do backend usa o JAR em `target/`; localmente, gere-o antes do
`docker build` com:

```powershell
.\mvnw.cmd --batch-mode clean package -DskipTests
docker build -t pilates-backend .
```

O workflow consulta o IP privado da EC2 do banco e atualiza o backend com esse
endereço.