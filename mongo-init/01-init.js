// Este script roda automaticamente quando o container sobe pela primeira vez

// Conectar no banco eduhub_db
db = db.getSiblingDB('eduhub_db');

// Criar um usuário para a aplicação
db.createUser({
  user: 'eduhub_user',
  pwd: 'eduhub_pass',
  roles: [
    {
      role: 'readWrite',
      db: 'eduhub_db'
    }
  ]
});

// Criar algumas collections iniciais com dados de exemplo
db.createCollection('users');
db.createCollection('courses');
db.createCollection('enrollments');

// Inserir um usuário administrador padrão
db.users.insertOne({
  name: "Admin System",
  email: "admin@eduhub.com",
  password: "$2a$10$dummyhash", // senha será hash real na aplicação
  profile: "ADMIN",
  active: true,
  createdAt: new Date()
});

// Inserir um curso de exemplo
db.courses.insertOne({
  title: "Introdução ao Spring Boot",
  description: "Curso completo de Spring Boot para iniciantes",
  category: "Programação",
  level: "BEGINNER",
  instructor: {
    id: ObjectId(),
    name: "João Silva",
    email: "joao@eduhub.com"
  },
  modules: [
    {
      title: "Configuração do Ambiente",
      order: 1,
      lessons: [
        {
          title: "Instalando Java 19",
          duration: 1800,
          order: 1,
          type: "VIDEO"
        },
        {
          title: "Configurando IDE",
          duration: 1200,
          order: 2,
          type: "VIDEO"
        }
      ]
    }
  ],
  price: 199.90,
  active: true,
  createdAt: new Date()
});

print("Database initialized successfully!");