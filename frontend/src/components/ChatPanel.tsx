
import { useState } from 'react'
import './ChatPanel.css'

type ChatMessage = {
    role: 'user' | 'assistant'
    content: string
}

type ChatResponse = {
    conversationId: string
    response: string
}

const suggestions = [
    'Find laptops under $1,000',
    'Compare Creator Laptop 14 and Studio Laptop 16',
    'Recommend a phone with a good camera',
]

export default function ChatPanel() {
    const [conversationId, setConversationId] = useState<string>(
        () => crypto.randomUUID()
    )
    const [messages, setMessages] = useState<ChatMessage[]>([])
    const [input, setInput] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    async function sendMessage(text: string) {
        const message = text.trim()
        if (!message || loading) return

        setMessages(previous => [
            ...previous,
            { role: 'user', content: message },
        ])
        setInput('')
        setError('')
        setLoading(true)

        try {
            const response = await fetch('/api/v1/chat', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    conversationId,
                    message,
                }),
            })

            if (!response.ok) {
                throw new Error(`Chat request failed: HTTP ${response.status}`)
            }

            const data: ChatResponse = await response.json()

            setConversationId(data.conversationId)

            setMessages(previous => [
                ...previous,
                { role: 'assistant', content: data.response },
            ])
        } catch (err) {
            setError(
                err instanceof Error
                    ? err.message
                    : 'Unable to contact the AI assistant'
            )
        } finally {
            setLoading(false)
        }
    }

    function newConversation() {
        if (loading) return
        setConversationId(crypto.randomUUID())
        setMessages([])
        setInput('')
        setError('')
    }

    return (
        <section className="chat-panel">
            <div className="chat-header">
                <div>
                    <h2>🤖 AI Shopping Assistant</h2>
                    <p>Powered by Spring AI + local Ollama</p>
                </div>

                <button
                    className="new-chat-button"
                    onClick={newConversation}
                    disabled={loading}
                >
                    New Chat
                </button>
            </div>

            <div className="chat-messages" aria-live="polite">
                {messages.length === 0 && (
                    <div className="chat-welcome">
                        <h3>How can I help you shop?</h3>
                        <p>
                            Ask me to find products, compare specifications,
                            or calculate savings.
                        </p>

                        <div className="chat-suggestions">
                            {suggestions.map(suggestion => (
                                <button
                                    key={suggestion}
                                    disabled={loading}
                                    onClick={() => void sendMessage(suggestion)}
                                >
                                    {suggestion}
                                </button>
                            ))}
                        </div>
                    </div>
                )}

                {messages.map((message, index) => (
                    <div
                        key={index}
                        className={`chat-message ${message.role}`}
                    >
                        {message.content}
                    </div>
                ))}

                {loading && (
                    <div className="chat-message assistant">
                        Thinking...
                    </div>
                )}
            </div>

            {error && <p className="chat-error">{error}</p>}

            <form
                className="chat-form"
                onSubmit={event => {
                    event.preventDefault()
                    void sendMessage(input)
                }}
            >
                <input
                    value={input}
                    onChange={event => setInput(event.target.value)}
                    placeholder="Ask about products..."
                    disabled={loading}
                    aria-label="Message the AI shopping assistant"
                />

                <button
                    type="submit"
                    disabled={loading || !input.trim()}
                >
                    Send
                </button>
            </form>
        </section>
    )
}
