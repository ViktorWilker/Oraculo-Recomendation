# Oráculo — Recomendação de Mídias

 Avalie filmes, jogos e livros. O Oráculo aprende seu gosto e recomenda o que você vai amar.

---

##  Sobre o projeto

O Oráculo é um app Android que randomiza mídias para o usuário avaliar com 1 a 5 estrelas. Com base nessas avaliações, um algoritmo de similaridade por cosseno gera recomendações personalizadas — garantindo ao menos 3 sugestões de cada categoria (filmes, jogos e livros).

O catálogo atual conta com **139 títulos** distribuídos entre as três categorias, carregados a partir de um arquivo JSON local.

---

## Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Kotlin |
| UI | Jetpack Compose |
| Estado | ViewModel + StateFlow |
| Imagens | Coil (AsyncImage) |
| Dados | JSON local (assets) |
| Build | Gradle (KTS) |

---

## Como o algoritmo funciona

O sistema usa **similaridade por cosseno** entre um vetor de preferências do usuário e vetores de cada item do catálogo

**Vetor do usuário** é construído a partir das avaliações:
- Ratings 4–5 somam peso positivo para tipo e tags
- Ratings 1–2 subtraem peso — itens com perfil similar são penalizados
- Rating 3 é neutro

**Score final** é normalizado para a faixa de **30–98%** para evitar matches irreais

**Garantia de diversidade:** independente do gosto detectado, o resultado sempre inclui no mínimo 3 filmes, 3 jogos e 3 livros

> O mock atual será substituído por um backend Python com ML real em uma próxima versão

---


## 🗺️ Roadmap

- [x] Avaliação de mídias com estrelas
- [x] Algoritmo de recomendação por similaridade de cosseno
- [x] Diversidade mínima por categoria
- [ ] Backend Python com ML (collaborative filtering)
- [ ] Autenticação e histórico de avaliações
- [ ] Integração com APIs externas (TMDB, RAWG, OpenLibrary)

---

## 📄 Licença

MIT
