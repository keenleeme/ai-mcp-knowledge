CREATE EXTENSION IF NOT EXISTS vector;

CREATE EXTENSION IF NOT EXISTS uuid-ossp;

-- ollama大模型使用的向量库
CREATE TABLE public.vector_store_ollama_deepseek (
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    "content" text NULL,
    metadata json NULL,
    embedding vector(768) NULL,
    CONSTRAINT vector_store_ollama_deepseek_pkey PRIMARY KEY (id)
);

-- openAi大模型使用的向量库
CREATE TABLE public.vector_store_openai (
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    "content" text NULL,
    metadata json NULL,
    embedding vector(1536) NULL,
    CONSTRAINT vector_store_openai_pkey PRIMARY KEY (id)
);

