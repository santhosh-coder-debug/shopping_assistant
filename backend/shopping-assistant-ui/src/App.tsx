
import { useEffect, useState } from 'react'
import ChatPanel from './components/ChatPanel'
import './App.css'

type Product = {
  id: number
  sku: string
  name: string
  brand: string
  category: string
  description: string
  price: number
  currency: string
  stockQuantity: number
  active: boolean
}

function App() {
  const [products, setProducts] = useState<Product[]>([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [selectedIds, setSelectedIds] = useState<number[]>([])
  const [showComparison, setShowComparison] = useState(false)

  useEffect(() => {
    async function loadProducts() {
      try {
        const response = await fetch('/api/v1/products')

        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`)
        }

        const data: Product[] = await response.json()
        setProducts(data)
      } catch (err) {
        setError(
            err instanceof Error ? err.message : 'Failed to load products'
        )
      } finally {
        setLoading(false)
      }
    }

    void loadProducts()
  }, [])

  const filteredProducts = products.filter(product =>
      `${product.name} ${product.brand} ${product.category}`
          .toLowerCase()
          .includes(search.toLowerCase())
  )

  const selectedProducts = selectedIds
      .map(id => products.find(product => product.id === id))
      .filter((product): product is Product => product !== undefined)

  const canCompare =
      selectedProducts.length === 2 &&
      selectedProducts[0].currency === selectedProducts[1].currency

  const priceDifference = canCompare
      ? Math.abs(
          selectedProducts[0].price - selectedProducts[1].price
      )
      : null

  const cheaperProduct = canCompare
      ? selectedProducts[0].price <= selectedProducts[1].price
          ? selectedProducts[0]
          : selectedProducts[1]
      : null

  function formatPrice(price: number, currency: string) {
    return price.toLocaleString('en-US', {
      style: 'currency',
      currency,
    })
  }

  function toggleProduct(id: number) {
    setSelectedIds(current => {
      if (current.includes(id)) {
        return current.filter(selectedId => selectedId !== id)
      }

      if (current.length >= 2) {
        return current
      }

      return [...current, id]
    })

    setShowComparison(false)
  }

  function clearComparison() {
    setSelectedIds([])
    setShowComparison(false)
  }

  return (
      <div className="app">
        <header className="header">
          <div className="logo">🛍️ ShopSmart AI</div>
          <span className="header-tag">Powered by local AI</span>
        </header>

        <main className="container">
          <section className="hero">
            <h1>Find your perfect product</h1>
            <p>
              Explore products and get personalized recommendations
              from your AI shopping assistant.
            </p>

            <input
                className="search"
                type="search"
                placeholder="Search laptops, phones, headphones..."
                value={search}
                onChange={event => setSearch(event.target.value)}
            />
          </section>

          <section className="catalog">
            <div className="catalog-heading">
              <h2>Our Products</h2>
              <span className="selection-count">
              {selectedIds.length}/2 selected for comparison
            </span>
            </div>

            {loading && <p>Loading products...</p>}

            {error && (
                <p className="error">
                  Could not load products: {error}.
                  Make sure Spring Boot is running on port 8080.
                </p>
            )}

            {!loading && !error && filteredProducts.length === 0 && (
                <p>No matching products found.</p>
            )}

            <div className="product-grid">
              {filteredProducts.map(product => {
                const selected = selectedIds.includes(product.id)

                return (
                    <article
                        className={`product-card ${selected ? 'selected-card' : ''}`}
                        key={product.id}
                    >
                      <div className="product-icon">
                        {product.category.toLowerCase() === 'laptop'
                            ? '💻'
                            : product.category.toLowerCase() === 'phone'
                                ? '📱'
                                : '🎧'}
                      </div>

                      <div className="product-info">
                    <span className="category">
                      {product.category}
                    </span>

                        <h3>{product.name}</h3>
                        <p className="brand">{product.brand}</p>
                        <p className="description">
                          {product.description}
                        </p>

                        <div className="product-footer">
                          <strong>
                            {formatPrice(product.price, product.currency)}
                          </strong>
                          <span className="stock">
                        {product.stockQuantity > 0
                            ? 'In stock'
                            : 'Out of stock'}
                      </span>
                        </div>

                        <label className="compare-checkbox">
                          <input
                              type="checkbox"
                              checked={selected}
                              disabled={
                                  !selected &&
                                  (selectedIds.length >= 2 ||
                                      !product.active ||
                                      product.stockQuantity <= 0)
                              }
                              onChange={() => toggleProduct(product.id)}
                          />
                          Compare this product
                        </label>
                      </div>
                    </article>
                )
              })}
            </div>

            {selectedIds.length > 0 && (
                <div className="comparison-actions">
              <span>
                {selectedIds.length === 1
                    ? 'Select one more product to compare.'
                    : 'Two products selected.'}
              </span>

                  <div className="comparison-buttons">
                    <button
                        type="button"
                        className="clear-button"
                        onClick={clearComparison}
                    >
                      Clear
                    </button>

                    <button
                        type="button"
                        className="compare-button"
                        disabled={selectedIds.length !== 2}
                        onClick={() => setShowComparison(true)}
                    >
                      Compare Selected
                    </button>
                  </div>
                </div>
            )}
          </section>

          {showComparison && selectedProducts.length === 2 && (
              <section className="comparison-section">
                <div className="comparison-heading">
                  <div>
                    <h2>Product Comparison</h2>
                    <p>Compare products from your live catalog.</p>
                  </div>

                  <button
                      type="button"
                      className="clear-button"
                      onClick={() => setShowComparison(false)}
                  >
                    Close
                  </button>
                </div>

                <div className="comparison-table-wrapper">
                  <table className="comparison-table">
                    <thead>
                    <tr>
                      <th>Feature</th>
                      {selectedProducts.map(product => (
                          <th key={product.id}>{product.name}</th>
                      ))}
                    </tr>
                    </thead>

                    <tbody>
                    <tr>
                      <td>Brand</td>
                      {selectedProducts.map(product => (
                          <td key={product.id}>{product.brand}</td>
                      ))}
                    </tr>

                    <tr>
                      <td>Category</td>
                      {selectedProducts.map(product => (
                          <td key={product.id}>{product.category}</td>
                      ))}
                    </tr>

                    <tr>
                      <td>Price</td>
                      {selectedProducts.map(product => (
                          <td key={product.id}>
                            <strong>
                              {formatPrice(product.price, product.currency)}
                            </strong>
                          </td>
                      ))}
                    </tr>

                    <tr>
                      <td>Specifications</td>
                      {selectedProducts.map(product => (
                          <td key={product.id}>
                            {product.description}
                          </td>
                      ))}
                    </tr>

                    <tr>
                      <td>Availability</td>
                      {selectedProducts.map(product => (
                          <td key={product.id}>
                            {product.stockQuantity > 0
                                ? 'In stock'
                                : 'Out of stock'}
                          </td>
                      ))}
                    </tr>
                    </tbody>
                  </table>
                </div>

                {canCompare && cheaperProduct && priceDifference !== null ? (
                    <div className="savings-banner">
                      {priceDifference === 0
                          ? 'Both products have the same price.'
                          : `💰 ${cheaperProduct.name} is cheaper by ${formatPrice(
                              priceDifference,
                              cheaperProduct.currency
                          )}.`}
                    </div>
                ) : (
                    <p className="comparison-note">
                      Price savings cannot be calculated across
                      different currencies.
                    </p>
                )}
              </section>
          )}

          <ChatPanel />
        </main>
      </div>
  )
}

export default App
