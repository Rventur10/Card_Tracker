# Pokémon Card Price Tracker

## Collection Application
With the explosive growth of the Pokémon trading card market, I've found bulk buying at conventions to be the most effective way to expand my collection. This Spring Boot application modernizes my original trading card tracker to help optimize purchasing decisions at conventions by aggregating pricing data from multiple sources.

## Business Application
This updated version will be written in Java and Kotlin via the Spring Boot framework. I plan to find the raw prices and sales volumes via TCGPlayer (PokemonTCG API) and the gradded counte parts via eBay API. Ebay will also be the backbone for finding the volume of sales.


## Architecture & Technology Stack

### Backend Framework
- **Spring Boot** - Core application framework
- **Spring Data JPA** - Data persistence layer
- **Maven** - Dependency management
- **H2/PostgreSQL** - Database (configurable)

### External Integrations
- **Pokémon TCG API** - Official card data and metadata (Updated model) - See dedicated branch
- **eBay API** - Real-time market pricing and webhook notifications
- **TCGPlayer** - Web scraping for additional price data (legacy implementation) - Currently on main


### Pokémon TCG API (Kotlin Service)
- Automatic pagination for complete set data
- Retry logic with exponential backoff

### Legacy Web Scraping (Playwright) 
- Robust element selection with fallback strategies
- Rate limiting for respectful server interaction


## Development Evolution

1. **Phase 1**: Web scraping implementation using Playwright for initial data acquisition //phased out
2. **Phase 2**: Migration to official APIs for improved reliability and compliance
3. **Phase 3**: Real-time webhook integration for dynamic market updates


## Future Enhancements

- **Angular Frontend** - Modern SPA for enhanced user experience
- **Full Ebay API integration**
- **Buy now pricing** - Pricing based on sale volumes on ebay.

